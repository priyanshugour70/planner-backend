package com.planner.dtos.res.sync;

import com.planner.dtos.res.auth.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullSyncResponse {
    private Integer version;
    private Long exportedAt;
    private UserResponse userProfile;
    private Map<String, Object> settings;
    private List<Map<String, Object>> goals;
    private List<Map<String, Object>> tasks;
    private List<Map<String, Object>> events;
    private List<Map<String, Object>> notes;
    private List<Map<String, Object>> habits;
    private List<Map<String, Object>> habitEntries;
    private List<Map<String, Object>> journalEntries;
    private List<Map<String, Object>> reminders;
    private List<Map<String, Object>> transactions;
    private List<Map<String, Object>> budgets;
    private List<Map<String, Object>> financeLogs;
}
