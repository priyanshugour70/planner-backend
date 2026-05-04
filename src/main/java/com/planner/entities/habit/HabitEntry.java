package com.planner.entities.habit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.HabitMood;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habit_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class HabitEntry extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "habit_id", nullable = false)
    private String habitId;

    @Column(name = "date", nullable = false)
    private Long date;

    @Column(name = "\"value\"")
    private Float value = 0f;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood")
    private HabitMood mood;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
