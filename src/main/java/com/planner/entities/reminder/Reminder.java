package com.planner.entities.reminder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.ItemPriority;
import com.planner.enums.RepeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Reminder extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "reminder_time", nullable = false)
    private Long reminderTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type")
    private RepeatType repeatType = RepeatType.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private ItemPriority priority = ItemPriority.P5;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "linked_note_id")
    private String linkedNoteId;

    @Column(name = "linked_task_id")
    private String linkedTaskId;

    @Column(name = "linked_goal_id")
    private String linkedGoalId;

    @Column(name = "color")
    private Long color = 0xFF6C63FFL;

    @Column(name = "notification_id")
    private Integer notificationId;
}
