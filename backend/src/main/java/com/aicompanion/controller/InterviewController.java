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
            @RequestParam(required = false) Long userId,
            @RequestBody InterviewStartDTO dto) {
        // 硬编码保底防御，防止未传 userId 时发生错误
        if (userId == null) {
            userId = 1L;
        }
        InterviewQuestionVO vo = interviewService.startInterview(userId, dto);
        return Result.success(vo);
    }

    @PostMapping("/answer")
    public Result<InterviewQuestionVO> submitAnswer(
            @RequestParam(required = false) Long userId,
            @RequestBody InterviewAnswerDTO dto) {
        if (userId == null) {
            userId = 1L;
        }
        InterviewQuestionVO vo = interviewService.submitAnswer(userId, dto);
        return Result.success(vo);
    }

    @GetMapping("/report/{sessionId}")
    public Result<InterviewReportVO> getInterviewReport(
            @RequestParam(required = false) Long userId,
            @PathVariable("sessionId") Long sessionId) {
        if (userId == null) {
            userId = 1L;
        }
        InterviewReportVO vo = interviewService.getInterviewReport(userId, sessionId);
        return Result.success(vo);
    }
}
