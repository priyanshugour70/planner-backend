package com.planner.enums;

import lombok.Getter;

@Getter
public enum GoalCategory {
    HEALTH("Health & Fitness"),
    CAREER("Career"),
    LEARNING("Learning"),
    COMMUNICATION("Communication"),
    LIFESTYLE("Lifestyle"),
    DISCIPLINE("Discipline"),
    FINANCE("Finance"),
    STARTUP("Startup"),
    MINDFULNESS("Mindfulness"),
    PERSONAL("Personal"),
    RELATIONSHIPS("Relationships"),
    CREATIVITY("Creativity"),
    FITNESS("Fitness"),
    TRAVEL("Travel"),
    OTHER("Other");

    private final String displayName;

    GoalCategory(String displayName) {
        this.displayName = displayName;
    }
}
