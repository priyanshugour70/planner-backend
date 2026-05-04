package com.planner.entities.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.ItemPriority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calendar_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class CalendarEvent extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date", nullable = false)
    private Long date;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "color")
    private Long color = 0xFF2196F3L;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private ItemPriority priority = ItemPriority.P5;

    @Column(name = "linked_goal_id")
    private String linkedGoalId;

    @Column(name = "linked_task_id")
    private String linkedTaskId;

    @Column(name = "linked_note_id")
    private String linkedNoteId;

    @Column(name = "linked_reminder_id")
    private String linkedReminderId;

    @Column(name = "is_all_day")
    private Boolean isAllDay = true;

    @Column(name = "reminder")
    private Long reminder;

    @Column(name = "reminder_enabled")
    private Boolean reminderEnabled = true;

    @Column(name = "notification_id")
    private Integer notificationId;
}
