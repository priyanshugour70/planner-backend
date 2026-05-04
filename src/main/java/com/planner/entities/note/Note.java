package com.planner.entities.note;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.ItemPriority;
import com.planner.enums.RepeatType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Note extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "color")
    private Long color = 0xFFFFFFFFL;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "linked_goal_id")
    private String linkedGoalId;

    @Column(name = "linked_task_id")
    private String linkedTaskId;

    @Column(name = "linked_reminder_id")
    private String linkedReminderId;

    @ElementCollection
    @CollectionTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private ItemPriority priority = ItemPriority.P6;

    @Column(name = "has_reminder")
    private Boolean hasReminder = false;

    @Column(name = "reminder_time")
    private Long reminderTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_repeat_type")
    private RepeatType reminderRepeatType = RepeatType.NONE;

    @Column(name = "is_reminder_enabled")
    private Boolean isReminderEnabled = true;

    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "category")
    private String category = "General";

    @Column(name = "mood")
    private String mood = "Neutral";

    @Column(name = "next_recall_date")
    private Long nextRecallDate;

    @Column(name = "recall_count")
    private Integer recallCount = 0;
}
