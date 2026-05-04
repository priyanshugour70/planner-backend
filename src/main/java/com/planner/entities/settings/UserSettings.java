package com.planner.entities.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.ThemeMode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class UserSettings extends BaseEntity {

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_mode")
    private ThemeMode themeMode = ThemeMode.SYSTEM;

    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;

    @Column(name = "daily_reminder_time")
    private String dailyReminderTime = "08:00";

    @Column(name = "weekly_review_day")
    private Integer weeklyReviewDay = 0;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "pin_code")
    private String pinCode;
}
