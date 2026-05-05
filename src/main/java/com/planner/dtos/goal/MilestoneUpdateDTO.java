package com.planner.dtos.goal;

import com.planner.enums.GoalPriority;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneUpdateDTO {

    @Size(max = 255, message = "Milestone title must be at most 255 characters")
    private String title;

    private String description;
    private Boolean isCompleted;
    private Long targetDate;
    private String quality;
    private Integer rating;
    private GoalPriority priority;
    private String notes;
    private String estimatedEffort;
    private String actualEffort;
    private String reflection;
    private Integer orderIndex;
}
