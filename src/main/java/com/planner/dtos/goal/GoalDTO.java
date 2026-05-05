package com.planner.dtos.goal;

import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDTO {
    private String uuid;
    private Integer number;
    private String title;
    private String description;
    private GoalCategory category;
    private GoalStatus status;
    private GoalPriority priority;
    private String icon;
    private Long color;
    private Float progress;
    private Long targetDate;
    private Long startDate;
    private Long completedDate;
    private String tags;
    private String notes;
    private Boolean isFavorite;
    private Boolean isPinned;
    private Boolean reminderEnabled;
    private String reminderFrequency;
    private String motivation;
    private String expectedOutcome;
    private List<MilestoneDTO> milestones;
    private int totalMilestones;
    private int completedMilestones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
