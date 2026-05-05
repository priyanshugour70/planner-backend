package com.planner.dtos.goal;

import com.planner.enums.GoalPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneCreateDTO {

    @NotBlank(message = "Milestone title is required")
    @Size(max = 255, message = "Milestone title must be at most 255 characters")
    private String title;

    private String description;
    private Long targetDate;
    private GoalPriority priority;
    private String notes;
    private String estimatedEffort;
    private Integer orderIndex;
}
