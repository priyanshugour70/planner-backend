package com.planner.service.analytics;

import com.planner.dtos.ServiceResult;

import java.util.Map;

public interface AnalyticsService {

    ServiceResult<Map<String, Object>> getDashboardStats();

    ServiceResult<Map<String, Object>> getGoalAnalytics();

    ServiceResult<Map<String, Object>> getTaskAnalytics();

    ServiceResult<Map<String, Object>> getHabitAnalytics();
}
