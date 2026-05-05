package com.planner.dtos.goal;

import com.planner.enums.GoalPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneDTO {
    private String uuid;
    private String title;
    private String description;
    private Boolean isCompleted;
    private Long completedAt;
    private Long targetDate;
    private String quality;
    private Integer rating;
    private Integer orderIndex;
    private GoalPriority priority;
    private String notes;
    private String estimatedEffort;
    private String actualEffort;
    private String reflection;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
