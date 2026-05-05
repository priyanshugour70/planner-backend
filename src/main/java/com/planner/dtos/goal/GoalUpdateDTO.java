package com.planner.dtos.goal;

import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalUpdateDTO {

    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    private GoalCategory category;
    private GoalStatus status;
    private GoalPriority priority;
    private String icon;
    private Long color;
    private Float progress;
    private Long targetDate;
    private Long startDate;
    private Integer number;
    private String tags;
    private String notes;
    private Boolean isFavorite;
    private Boolean isPinned;
    private String motivation;
    private String expectedOutcome;
    private Boolean reminderEnabled;
    private String reminderFrequency;
}
