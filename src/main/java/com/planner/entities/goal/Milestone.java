package com.planner.entities.goal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planner.entities.BaseEntity;
import com.planner.enums.GoalPriority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "milestones", indexes = {
        @Index(name = "idx_milestones_goal", columnList = "goal_id"),
        @Index(name = "idx_milestones_completed", columnList = "is_completed, active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Milestone extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    @JsonIgnore
    private Goal goal;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private Long completedAt;

    @Column(name = "target_date")
    private Long targetDate;

    @Column(name = "quality")
    private String quality;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    @Builder.Default
    private GoalPriority priority = GoalPriority.MEDIUM;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "estimated_effort")
    private String estimatedEffort;

    @Column(name = "actual_effort")
    private String actualEffort;

    @Column(name = "reflection", columnDefinition = "TEXT")
    private String reflection;
}
