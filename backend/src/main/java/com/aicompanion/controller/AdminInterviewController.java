package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.entity.InterviewSession;
import com.aicompanion.entity.User;
import com.aicompanion.service.InterviewService;
import com.aicompanion.service.UserService;
import com.aicompanion.vo.InterviewReportVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/interviews")
@RequiredArgsConstructor
public class AdminInterviewController {

    private final InterviewService interviewService;
    private final UserService userService;

    @GetMapping("/history")
    public Result<Map<String, Object>> getGlobalHistory(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        Page<InterviewSession> sessionPage = interviewService.getGlobalInterviewHistory(page, size);
        
        // Enrich with username for admin view
        List<Map<String, Object>> enrichedList = sessionPage.getRecords().stream().map(session -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", session.getId());
            map.put("userId", session.getUserId());
            map.put("jobPosition", session.getJobPosition());
            map.put("score", session.getScore());
            map.put("status", session.getStatus());
            
            User user = userService.getById(session.getUserId());
            map.put("username", user != null ? user.getUsername() : "未知用户");
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", enrichedList);
        result.put("total", sessionPage.getTotal());
        return Result.success(result);
    }

    @GetMapping("/report/{sessionId}")
    public Result<InterviewReportVO> getGlobalReport(@PathVariable("sessionId") Long sessionId) {
        return Result.success(interviewService.getGlobalInterviewReport(sessionId));
    }
}
