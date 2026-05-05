package com.planner.entities.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.ItemPriority;
import com.planner.enums.RepeatType;
import com.planner.enums.TaskPriority;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Task extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_priority")
    private ItemPriority itemPriority = ItemPriority.P5;

    @Column(name = "due_date")
    private Long dueDate;

    @Column(name = "linked_goal_id")
    private String linkedGoalId;

    @Column(name = "linked_note_id")
    private String linkedNoteId;

    @Column(name = "linked_reminder_id")
    private String linkedReminderId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_tags", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type")
    private RepeatType repeatType = RepeatType.NONE;

    @Column(name = "reminder")
    private Long reminder;

    @Column(name = "reminder_enabled")
    private Boolean reminderEnabled = true;

    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "completed_at")
    private Long completedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Subtask> subtasks = new ArrayList<>();
}
