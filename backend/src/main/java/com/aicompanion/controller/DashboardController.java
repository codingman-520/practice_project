package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        return Result.success(dashboardService.getDashboardStats());
    }

    @GetMapping("/category-distribution")
    public Result<List<Map<String, Object>>> getCategoryDistribution() {
        return Result.success(dashboardService.getCategoryDistribution());
    }

    @GetMapping("/ai-call-trend")
    public Result<Map<String, Object>> getAiCallTrend() {
        return Result.success(dashboardService.getAiCallTrend());
    }

    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivities() {
        return Result.success(dashboardService.getRecentActivities());
    }
}
