package com.aicompanion.service;

import com.aicompanion.dto.InterviewAnswerDTO;
import com.aicompanion.dto.InterviewStartDTO;
import com.aicompanion.vo.InterviewQuestionVO;
import com.aicompanion.vo.InterviewReportVO;

public interface InterviewService {
    
    InterviewQuestionVO startInterview(Long userId, InterviewStartDTO dto);
    
    InterviewQuestionVO submitAnswer(Long userId, InterviewAnswerDTO dto);
    
    InterviewReportVO getInterviewReport(Long userId, Long sessionId);
}
