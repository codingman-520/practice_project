package com.aicompanion.service.impl;

import com.aicompanion.config.DifyConfig;
import com.aicompanion.dto.InterviewAnswerDTO;
import com.aicompanion.dto.InterviewStartDTO;
import com.aicompanion.entity.InterviewQuestion;
import com.aicompanion.entity.InterviewSession;
import com.aicompanion.mapper.InterviewQuestionMapper;
import com.aicompanion.mapper.InterviewSessionMapper;
import com.aicompanion.service.InterviewService;
import com.aicompanion.vo.InterviewQuestionVO;
import com.aicompanion.vo.InterviewReportVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewSessionMapper sessionMapper;
    private final InterviewQuestionMapper questionMapper;
    private final RestTemplate restTemplate;
    private final DifyConfig difyConfig;

    @Override
    public InterviewQuestionVO startInterview(Long userId, InterviewStartDTO dto) {
        // 1. 初始化会话
        InterviewSession session = InterviewSession.builder()
                .userId(userId)
                .jobPosition(dto.getJobPosition())
                .status("IN_PROGRESS")
                .build();
        sessionMapper.insert(session);

        // 2. 生成面试题目（目前为本地策略兜底，后续将对接管理端题库配置或大模型动态生成）
        List<String> questions = generateQuestionsByPosition(dto.getJobPosition());

        for (String qText : questions) {
            InterviewQuestion question = InterviewQuestion.builder()
                    .sessionId(session.getId())
                    .questionText(qText)
                    .build();
            questionMapper.insert(question);
        }

        // 3. 返回第一题
        return getNextQuestion(session.getId());
    }

    @Override
    public InterviewQuestionVO submitAnswer(Long userId, InterviewAnswerDTO dto) {
        // 校验会话所属权，防止越权操作
        InterviewSession session = sessionMapper.selectById(dto.getSessionId());
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("面试会话不存在或无权访问");
        }

        // 1. 寻找当前正在回答的题目
        LambdaQueryWrapper<InterviewQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterviewQuestion::getSessionId, dto.getSessionId())
                    .isNull(InterviewQuestion::getUserAnswer)
                    .orderByAsc(InterviewQuestion::getId)
                    .last("LIMIT 1");
        
        InterviewQuestion currentQuestion = questionMapper.selectOne(queryWrapper);

        if (currentQuestion == null) {
            // 如果所有题目都已经答完，再次提交答案应该直接返回结束标志，不应该报错
            InterviewQuestionVO completedVO = getNextQuestion(dto.getSessionId());
            if (Boolean.TRUE.equals(completedVO.getIsCompleted())) {
                return completedVO;
            }
            throw new RuntimeException("当前会话已结束或题目不存在");
        }

        // 2. 保存用户答案
        currentQuestion.setUserAnswer(dto.getAnswer());
        questionMapper.updateById(currentQuestion);

        // 3. 尝试获取下一题
        InterviewQuestionVO nextQuestionVO = getNextQuestion(dto.getSessionId());

        // 4. 如果下一题不存在（即所有题目均已答完），将状态设为 COMPLETED
        if (Boolean.TRUE.equals(nextQuestionVO.getIsCompleted())) {
            session = sessionMapper.selectById(dto.getSessionId());
            if (session != null && !"COMPLETED".equals(session.getStatus())) {
                // 关键修改：无论前端怎么作答，只要答完最后一题，立刻扭转状态为 COMPLETED
                session.setStatus("COMPLETED");
                sessionMapper.updateById(session);
                log.info("会话 SessionID: {} 答题结束，状态已变更为 COMPLETED", dto.getSessionId());
            }
        }

        return nextQuestionVO;
    }

    @Override
    public InterviewReportVO getInterviewReport(Long userId, Long sessionId) {
        InterviewSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("面试会话不存在或无权访问");
        }

        if (!"COMPLETED".equals(session.getStatus())) {
            throw new RuntimeException("面试尚未完成，无法生成报告");
        }

        // 如果已经生成过报告，直接返回缓存结果
        if (session.getScore() != null) {
            return InterviewReportVO.builder()
                    .sessionId(session.getId())
                    .jobPosition(session.getJobPosition())
                    .score(session.getScore())
                    .feedback(session.getFeedback())
                    .build();
        }

        // 获取所有题目和答案
        LambdaQueryWrapper<InterviewQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterviewQuestion::getSessionId, sessionId)
                    .orderByAsc(InterviewQuestion::getId);
        List<InterviewQuestion> questions = questionMapper.selectList(queryWrapper);

        // 拼接发给 Dify 的 Prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("请作为一个资深技术面试官，根据以下候选人的面试问答记录，给出一个综合评分（0-100）和详细的改进评语。\n");
        prompt.append("岗位：").append(session.getJobPosition()).append("\n\n");
        for (int i = 0; i < questions.size(); i++) {
            InterviewQuestion q = questions.get(i);
            prompt.append("问题 ").append(i + 1).append(": ").append(q.getQuestionText()).append("\n");
            prompt.append("回答: ").append(q.getUserAnswer() == null ? "未作答" : q.getUserAnswer()).append("\n\n");
        }
        prompt.append("请严格以 JSON 格式返回，必须包含 score (整数) 和 feedback (字符串) 两个字段。");

        // 调用 Dify Workflow API
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyConfig.getWorkflow().getApiKey());

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("interview_content", prompt.toString()); // 根据报错日志，将 query 改为 interview_content
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", userId.toString());

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            String url = difyConfig.getWorkflow().getBaseUrl() + "/workflows/run";

            log.info("正在调用 Dify 接口生成报告...");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                // 兼容性极强的 JSON 解析
                InterviewReportVO reportVO = parseDifyResponse(responseBody, session);
                sessionMapper.updateById(session); // 更新数据库中的 score 和 feedback
                return reportVO;
            }
            throw new RuntimeException("Dify API 响应为空");
        } catch (Exception e) {
            log.error("调用 Dify API 生成报告失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成面试报告失败: " + e.getMessage(), e);
        }
    }

    private InterviewReportVO parseDifyResponse(Map<String, Object> responseBody, InterviewSession session) {
        try {
            Integer score = 0;
            String feedback = "";
            
            if (responseBody.containsKey("data")) {
                // 处理 Workflow 接口标准返回
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data != null && data.containsKey("outputs")) {
                    Map<String, Object> outputs = (Map<String, Object>) data.get("outputs");
                    if (outputs.containsKey("score")) {
                        score = Integer.valueOf(outputs.get("score").toString());
                    }
                    if (outputs.containsKey("feedback")) {
                        feedback = outputs.get("feedback").toString();
                    } else {
                        feedback = outputs.toString();
                    }
                }
            } else if (responseBody.containsKey("answer")) {
                // 兼容处理 Chatflow 接口的返回
                String answer = (String) responseBody.get("answer");
                int start = answer.indexOf("{");
                int end = answer.lastIndexOf("}");
                if (start != -1 && end != -1 && end > start) {
                    String jsonStr = answer.substring(start, end + 1);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(jsonStr);
                    score = node.has("score") ? node.get("score").asInt() : 0;
                    feedback = node.has("feedback") ? node.get("feedback").asText() : answer;
                } else {
                    feedback = answer;
                }
            }
            
            session.setScore(score);
            session.setFeedback(feedback);
            return InterviewReportVO.builder()
                    .sessionId(session.getId())
                    .jobPosition(session.getJobPosition())
                    .score(score)
                    .feedback(feedback)
                    .build();
        } catch (Exception e) {
            log.error("解析 Dify 响应 JSON 失败", e);
            session.setScore(0);
            session.setFeedback("AI 评估生成失败，请稍后再试。");
            return InterviewReportVO.builder()
                    .sessionId(session.getId())
                    .jobPosition(session.getJobPosition())
                    .score(0)
                    .feedback("AI 评估生成失败，请稍后再试。")
                    .build();
        }
    }

    @Override
    public List<InterviewSession> getInterviewHistory(Long userId) {
        LambdaQueryWrapper<InterviewSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterviewSession::getUserId, userId)
                    .orderByDesc(InterviewSession::getId);
        return sessionMapper.selectList(queryWrapper);
    }

    private InterviewQuestionVO getNextQuestion(Long sessionId) {
        LambdaQueryWrapper<InterviewQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterviewQuestion::getSessionId, sessionId)
                    .orderByAsc(InterviewQuestion::getId);
        
        List<InterviewQuestion> allQuestions = questionMapper.selectList(queryWrapper);

        int totalCount = allQuestions.size();
        int currentIndex = 1;
        InterviewQuestion nextQ = null;

        for (InterviewQuestion q : allQuestions) {
            if (q.getUserAnswer() == null) {
                nextQ = q;
                break;
            }
            currentIndex++;
        }

        if (nextQ == null) {
            // 已全部答完
            return InterviewQuestionVO.builder()
                    .sessionId(sessionId)
                    .isCompleted(true)
                    .build();
        }

        // 还有未答的题目
        return InterviewQuestionVO.builder()
                .sessionId(sessionId)
                .questionId(nextQ.getId())
                .questionText(nextQ.getQuestionText())
                .currentIndex(currentIndex)
                .totalCount(totalCount)
                .isCompleted(false)
                .build();
    }

    private List<String> generateQuestionsByPosition(String jobPosition) {
        Map<String, String[]> questionBank = new HashMap<>();
        questionBank.put("FRONTEND", new String[]{
            "请简述 Vue3 和 Vue2 的核心区别，以及 Composition API 的优势？",
            "谈谈你对前端性能优化的理解，平时项目中做过哪些优化？",
            "在跨域场景下，常用的解决方案有哪些？原理是什么？"
        });
        questionBank.put("BACKEND", new String[]{
            "请简述 Spring Boot 的自动装配原理是什么？",
            "MySQL 索引失效的场景有哪些？如何进行 SQL 调优？",
            "Redis 常见的缓存穿透、击穿、雪崩现象是什么？如何解决？"
        });
        questionBank.put("DATABASE", new String[]{
            "请解释事务的 ACID 特性以及隔离级别？",
            "MySQL 中 InnoDB 引擎的 B+ 树索引结构是怎样的？",
            "什么是死锁？在数据库中如何避免死锁？"
        });

        String job = jobPosition != null ? jobPosition.toUpperCase() : "";
        String[] presets = questionBank.getOrDefault(job, new String[]{
            "请简述你对 " + jobPosition + " 相关技术的理解，以及你的核心优势？",
            "在过往的学习或项目经历中，你遇到过最大的技术挑战是什么？是如何解决的？",
            "如果团队中出现了意见分歧，或者需求不明确，你会如何处理？"
        });
        
        return List.of(presets);
    }
}
