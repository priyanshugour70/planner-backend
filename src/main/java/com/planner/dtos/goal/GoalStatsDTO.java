package com.planner.dtos.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalStatsDTO {
    private long totalGoals;
    private long completedGoals;
    private long inProgressGoals;
    private long notStartedGoals;
    private long onHoldGoals;
    private long abandonedGoals;
    private long totalMilestones;
    private long completedMilestones;
    private double averageProgress;
    private long favoriteGoals;
    private long overdueGoals;
    private Map<String, Long> goalsByCategory;
    private Map<String, Long> goalsByPriority;
}
