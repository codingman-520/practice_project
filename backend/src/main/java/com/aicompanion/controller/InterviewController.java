package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.dto.InterviewAnswerDTO;
import com.aicompanion.dto.InterviewStartDTO;
import com.aicompanion.service.InterviewService;
import com.aicompanion.vo.InterviewQuestionVO;
import com.aicompanion.vo.InterviewReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public Result<InterviewQuestionVO> startInterview(
            @RequestBody InterviewStartDTO dto) {
        Long userId = com.aicompanion.common.UserContext.getUserId();
        InterviewQuestionVO vo = interviewService.startInterview(userId, dto);
        return Result.success(vo);
    }

    @PostMapping("/answer")
    public Result<InterviewQuestionVO> submitAnswer(
            @RequestBody InterviewAnswerDTO dto) {
        Long userId = com.aicompanion.common.UserContext.getUserId();
        InterviewQuestionVO vo = interviewService.submitAnswer(userId, dto);
        return Result.success(vo);
    }

    @GetMapping("/report/{sessionId}")
    public Result<InterviewReportVO> getInterviewReport(
            @PathVariable("sessionId") Long sessionId) {
        Long userId = com.aicompanion.common.UserContext.getUserId();
        InterviewReportVO vo = interviewService.getInterviewReport(userId, sessionId);
        return Result.success(vo);
    }

    @GetMapping("/history")
    public Result<java.util.List<com.aicompanion.entity.InterviewSession>> getInterviewHistory() {
        Long userId = com.aicompanion.common.UserContext.getUserId();
        return Result.success(interviewService.getInterviewHistory(userId));
    }
}
