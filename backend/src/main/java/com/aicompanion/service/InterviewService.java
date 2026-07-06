package com.aicompanion.service;

import com.aicompanion.dto.InterviewAnswerDTO;
import com.aicompanion.dto.InterviewStartDTO;
import com.aicompanion.vo.InterviewQuestionVO;
import com.aicompanion.vo.InterviewReportVO;
import com.aicompanion.entity.InterviewSession;

import java.util.List;

public interface InterviewService {
    InterviewQuestionVO startInterview(Long userId, InterviewStartDTO dto);
    InterviewQuestionVO submitAnswer(Long userId, InterviewAnswerDTO dto);
    InterviewReportVO getInterviewReport(Long userId, Long sessionId);
    List<InterviewSession> getInterviewHistory(Long userId);
    
    // For Admin Web
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<InterviewSession> getGlobalInterviewHistory(Integer page, Integer size);
    InterviewReportVO getGlobalInterviewReport(Long sessionId);
}
