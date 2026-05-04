package com.planner.entities.habit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.HabitTimeOfDay;
import com.planner.enums.HabitType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Habit extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "goal_id")
    private String goalId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon")
    private String icon = "✨";

    @Column(name = "icon_color")
    private Long iconColor = 0xFF4DD0E1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private HabitType type = HabitType.YES_NO;

    @Column(name = "target_value")
    private Float targetValue = 1f;

    @Column(name = "unit")
    private String unit;

    @ElementCollection
    @CollectionTable(name = "habit_frequency", joinColumns = @JoinColumn(name = "habit_id"))
    @Column(name = "day_of_week")
    @Builder.Default
    private List<Integer> frequency = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7));

    @Enumerated(EnumType.STRING)
    @Column(name = "time_of_day")
    private HabitTimeOfDay timeOfDay = HabitTimeOfDay.ANY_TIME;

    @Column(name = "reminder_time")
    private String reminderTime;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
