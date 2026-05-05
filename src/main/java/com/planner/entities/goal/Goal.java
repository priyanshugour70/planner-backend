package com.planner.entities.goal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goals", indexes = {
        @Index(name = "idx_goals_user_active", columnList = "user_id, active"),
        @Index(name = "idx_goals_user_status", columnList = "user_id, status"),
        @Index(name = "idx_goals_user_category", columnList = "user_id, category"),
        @Index(name = "idx_goals_user_favorite", columnList = "user_id, is_favorite"),
        @Index(name = "idx_goals_user_priority", columnList = "user_id, priority")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Goal extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "number")
    private Integer number;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GoalCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    @Builder.Default
    private GoalPriority priority = GoalPriority.MEDIUM;

    @Column(name = "icon")
    private String icon;

    @Column(name = "color")
    private Long color;

    @Column(name = "progress")
    @Builder.Default
    private Float progress = 0f;

    @Column(name = "target_date")
    private Long targetDate;

    @Column(name = "start_date")
    private Long startDate;

    @Column(name = "completed_date")
    private Long completedDate;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_favorite")
    @Builder.Default
    private Boolean isFavorite = false;

    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "reminder_enabled")
    @Builder.Default
    private Boolean reminderEnabled = false;

    @Column(name = "reminder_frequency")
    private String reminderFrequency;

    @Column(name = "motivation", columnDefinition = "TEXT")
    private String motivation;

    @Column(name = "expected_outcome", columnDefinition = "TEXT")
    private String expectedOutcome;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<Milestone> milestones = new ArrayList<>();
}
