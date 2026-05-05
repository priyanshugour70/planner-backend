package com.planner.dtos.goal;

import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCreateDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private GoalCategory category;

    private GoalPriority priority;
    private String icon;
    private Long color;
    private Long targetDate;
    private Long startDate;
    private String tags;
    private String notes;
    private String motivation;
    private String expectedOutcome;
    private Boolean reminderEnabled;
    private String reminderFrequency;
    private List<MilestoneCreateDTO> milestones;
}
