package com.planner.entities.journal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.JournalMood;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class JournalEntry extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "date", nullable = false)
    private Long date;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood")
    private JournalMood mood = JournalMood.NEUTRAL;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_tags", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_linked_goals", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "goal_id")
    @Builder.Default
    private List<String> linkedGoalIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_linked_tasks", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "task_id")
    @Builder.Default
    private List<String> linkedTaskIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_photos", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "photo_url")
    @Builder.Default
    private List<String> photos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_gratitude", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "item")
    @Builder.Default
    private List<String> gratitude = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_achievements", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "item")
    @Builder.Default
    private List<String> achievements = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "journal_challenges", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "item")
    @Builder.Default
    private List<String> challenges = new ArrayList<>();

    @Column(name = "reflection", columnDefinition = "TEXT")
    private String reflection;
}
