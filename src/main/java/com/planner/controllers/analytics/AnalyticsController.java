package com.planner.controllers.analytics;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.service.analytics.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Dashboard and analytics data")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard overview analytics")
    public ResponseEntity<APIResponse<Map<String, Object>>> getDashboard() {
        ServiceResult<Map<String, Object>> result = analyticsService.getDashboardStats();
        return toApiResponse(result, "Dashboard data retrieved successfully");
    }

    @GetMapping("/goals")
    @Operation(summary = "Get goal analytics")
    public ResponseEntity<APIResponse<Map<String, Object>>> getGoalAnalytics() {
        ServiceResult<Map<String, Object>> result = analyticsService.getGoalAnalytics();
        return toApiResponse(result, "Goal analytics retrieved successfully");
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get task analytics")
    public ResponseEntity<APIResponse<Map<String, Object>>> getTaskAnalytics() {
        ServiceResult<Map<String, Object>> result = analyticsService.getTaskAnalytics();
        return toApiResponse(result, "Task analytics retrieved successfully");
    }

    @GetMapping("/habits")
    @Operation(summary = "Get habit analytics")
    public ResponseEntity<APIResponse<Map<String, Object>>> getHabitAnalytics() {
        ServiceResult<Map<String, Object>> result = analyticsService.getHabitAnalytics();
        return toApiResponse(result, "Habit analytics retrieved successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
