package com.aicompanion.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardStats();
    List<Map<String, Object>> getCategoryDistribution();
    Map<String, Object> getAiCallTrend();
    List<Map<String, Object>> getRecentActivities();
}
