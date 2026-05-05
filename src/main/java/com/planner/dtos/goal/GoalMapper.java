package com.planner.dtos.goal;

import com.planner.entities.goal.Goal;
import com.planner.entities.goal.Milestone;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public final class GoalMapper {

    private GoalMapper() {}

    public static GoalDTO toDTO(Goal goal) {
        List<MilestoneDTO> milestoneDTOs = goal.getMilestones() != null
                ? goal.getMilestones().stream()
                .filter(m -> Boolean.TRUE.equals(m.getActive()))
                .map(GoalMapper::toMilestoneDTO)
                .toList()
                : Collections.emptyList();

        long completed = milestoneDTOs.stream().filter(m -> Boolean.TRUE.equals(m.getIsCompleted())).count();

        return GoalDTO.builder()
                .uuid(goal.getUuid())
                .number(goal.getNumber())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .category(goal.getCategory())
                .status(goal.getStatus())
                .priority(goal.getPriority())
                .icon(goal.getIcon())
                .color(goal.getColor())
                .progress(goal.getProgress())
                .targetDate(goal.getTargetDate())
                .startDate(goal.getStartDate())
                .completedDate(goal.getCompletedDate())
                .tags(goal.getTags())
                .notes(goal.getNotes())
                .isFavorite(goal.getIsFavorite())
                .isPinned(goal.getIsPinned())
                .reminderEnabled(goal.getReminderEnabled())
                .reminderFrequency(goal.getReminderFrequency())
                .motivation(goal.getMotivation())
                .expectedOutcome(goal.getExpectedOutcome())
                .milestones(milestoneDTOs)
                .totalMilestones(milestoneDTOs.size())
                .completedMilestones((int) completed)
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }

    public static MilestoneDTO toMilestoneDTO(Milestone m) {
        return MilestoneDTO.builder()
                .uuid(m.getUuid())
                .title(m.getTitle())
                .description(m.getDescription())
                .isCompleted(m.getIsCompleted())
                .completedAt(m.getCompletedAt())
                .targetDate(m.getTargetDate())
                .quality(m.getQuality())
                .rating(m.getRating())
                .orderIndex(m.getOrderIndex())
                .priority(m.getPriority())
                .notes(m.getNotes())
                .estimatedEffort(m.getEstimatedEffort())
                .actualEffort(m.getActualEffort())
                .reflection(m.getReflection())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    public static Goal toEntity(GoalCreateDTO dto, Long userId) {
        Goal goal = Goal.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .priority(dto.getPriority() != null ? dto.getPriority() : GoalPriority.MEDIUM)
                .status(GoalStatus.NOT_STARTED)
                .icon(dto.getIcon())
                .color(dto.getColor())
                .progress(0f)
                .targetDate(dto.getTargetDate())
                .startDate(dto.getStartDate())
                .tags(dto.getTags())
                .notes(dto.getNotes())
                .motivation(dto.getMotivation())
                .expectedOutcome(dto.getExpectedOutcome())
                .reminderEnabled(dto.getReminderEnabled() != null ? dto.getReminderEnabled() : false)
                .reminderFrequency(dto.getReminderFrequency())
                .isFavorite(false)
                .isPinned(false)
                .build();

        goal.setActive(true);
        goal.setCreatedBy(userId);

        if (dto.getMilestones() != null && !dto.getMilestones().isEmpty()) {
            List<MilestoneCreateDTO> mDtos = dto.getMilestones();
            IntStream.range(0, mDtos.size()).forEach(i -> {
                Milestone m = toMilestoneEntity(mDtos.get(i), userId);
                m.setOrderIndex(mDtos.get(i).getOrderIndex() != null ? mDtos.get(i).getOrderIndex() : i);
                m.setGoal(goal);
                goal.getMilestones().add(m);
            });
        }

        return goal;
    }

    public static Milestone toMilestoneEntity(MilestoneCreateDTO dto, Long userId) {
        Milestone m = Milestone.builder()
                .uuid(UUID.randomUUID().toString())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .targetDate(dto.getTargetDate())
                .priority(dto.getPriority() != null ? dto.getPriority() : GoalPriority.MEDIUM)
                .notes(dto.getNotes())
                .estimatedEffort(dto.getEstimatedEffort())
                .isCompleted(false)
                .orderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0)
                .build();

        m.setActive(true);
        m.setCreatedBy(userId);
        return m;
    }

    public static void applyUpdate(Goal existing, GoalUpdateDTO dto, Long userId) {
        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getCategory() != null) existing.setCategory(dto.getCategory());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
            if (dto.getStatus() == GoalStatus.COMPLETED && existing.getCompletedDate() == null) {
                existing.setCompletedDate(System.currentTimeMillis());
                existing.setProgress(1.0f);
            }
            if (dto.getStatus() == GoalStatus.IN_PROGRESS && existing.getStartDate() == null) {
                existing.setStartDate(System.currentTimeMillis());
            }
        }
        if (dto.getPriority() != null) existing.setPriority(dto.getPriority());
        if (dto.getIcon() != null) existing.setIcon(dto.getIcon());
        if (dto.getColor() != null) existing.setColor(dto.getColor());
        if (dto.getProgress() != null) existing.setProgress(dto.getProgress());
        if (dto.getTargetDate() != null) existing.setTargetDate(dto.getTargetDate());
        if (dto.getStartDate() != null) existing.setStartDate(dto.getStartDate());
        if (dto.getNumber() != null) existing.setNumber(dto.getNumber());
        if (dto.getTags() != null) existing.setTags(dto.getTags());
        if (dto.getNotes() != null) existing.setNotes(dto.getNotes());
        if (dto.getIsFavorite() != null) existing.setIsFavorite(dto.getIsFavorite());
        if (dto.getIsPinned() != null) existing.setIsPinned(dto.getIsPinned());
        if (dto.getMotivation() != null) existing.setMotivation(dto.getMotivation());
        if (dto.getExpectedOutcome() != null) existing.setExpectedOutcome(dto.getExpectedOutcome());
        if (dto.getReminderEnabled() != null) existing.setReminderEnabled(dto.getReminderEnabled());
        if (dto.getReminderFrequency() != null) existing.setReminderFrequency(dto.getReminderFrequency());
        existing.setUpdatedBy(userId);
    }

    public static void applyMilestoneUpdate(Milestone existing, MilestoneUpdateDTO dto, Long userId) {
        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getIsCompleted() != null) {
            existing.setIsCompleted(dto.getIsCompleted());
            existing.setCompletedAt(dto.getIsCompleted() ? System.currentTimeMillis() : null);
        }
        if (dto.getTargetDate() != null) existing.setTargetDate(dto.getTargetDate());
        if (dto.getQuality() != null) existing.setQuality(dto.getQuality());
        if (dto.getRating() != null) existing.setRating(dto.getRating());
        if (dto.getPriority() != null) existing.setPriority(dto.getPriority());
        if (dto.getNotes() != null) existing.setNotes(dto.getNotes());
        if (dto.getEstimatedEffort() != null) existing.setEstimatedEffort(dto.getEstimatedEffort());
        if (dto.getActualEffort() != null) existing.setActualEffort(dto.getActualEffort());
        if (dto.getReflection() != null) existing.setReflection(dto.getReflection());
        if (dto.getOrderIndex() != null) existing.setOrderIndex(dto.getOrderIndex());
        existing.setUpdatedBy(userId);
    }
}
