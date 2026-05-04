package com.planner.service.habit.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.habit.Habit;
import com.planner.entities.habit.HabitEntry;
import com.planner.repositories.habit.HabitEntryRepository;
import com.planner.repositories.habit.HabitRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.habit.HabitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitEntryRepository habitEntryRepository;

    @Override
    public ServiceResult<Habit> createHabit(Habit habit) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating habit for user: {}", userId);

        habit.setUuid(UUID.randomUUID().toString());
        habit.setUserId(userId);
        habit.setCreatedBy(userId);
        habit.setActive(true);

        if (habit.getIsActive() == null) habit.setIsActive(true);
        if (habit.getTargetValue() == null) habit.setTargetValue(1f);

        Habit saved = habitRepository.save(habit);
        log.info("Habit created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    public ServiceResult<Habit> updateHabit(String uuid, Habit habit) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating habit uuid: {} for user: {}", uuid, userId);

        return habitRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (habit.getTitle() != null) existing.setTitle(habit.getTitle());
                    if (habit.getDescription() != null) existing.setDescription(habit.getDescription());
                    if (habit.getIcon() != null) existing.setIcon(habit.getIcon());
                    if (habit.getIconColor() != null) existing.setIconColor(habit.getIconColor());
                    if (habit.getType() != null) existing.setType(habit.getType());
                    if (habit.getTargetValue() != null) existing.setTargetValue(habit.getTargetValue());
                    if (habit.getUnit() != null) existing.setUnit(habit.getUnit());
                    if (habit.getFrequency() != null) existing.setFrequency(habit.getFrequency());
                    if (habit.getTimeOfDay() != null) existing.setTimeOfDay(habit.getTimeOfDay());
                    if (habit.getReminderTime() != null) existing.setReminderTime(habit.getReminderTime());
                    if (habit.getIsActive() != null) existing.setIsActive(habit.getIsActive());
                    if (habit.getGoalId() != null) existing.setGoalId(habit.getGoalId());
                    existing.setUpdatedBy(userId);

                    Habit updated = habitRepository.save(existing);
                    log.info("Habit updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Habit not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Habit not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteHabit(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting habit uuid: {} for user: {}", uuid, userId);

        return habitRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    habitRepository.save(existing);
                    log.info("Habit soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Habit not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Habit not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Habit> getHabitByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching habit uuid: {} for user: {}", uuid, userId);

        return habitRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(ServiceResult::ok)
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Habit not found"))));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<Habit>> getAllHabits(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all habits for user: {} page: {} size: {}", userId, page, size);

        Page<Habit> habitPage = habitRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        Pagination<Habit> pagination = Pagination.of(habitPage.getContent(), page, size, habitPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Habit>> getActiveHabits() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching active habits for user: {}", userId);

        List<Habit> habits = habitRepository.findActiveHabits(userId);
        return ServiceResult.ok(habits);
    }

    @Override
    public ServiceResult<HabitEntry> logHabitEntry(HabitEntry entry) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Logging habit entry for habit: {} on date: {}", entry.getHabitId(), entry.getDate());

        Optional<Habit> habitOpt = habitRepository.findByUuidAndUserIdAndActiveTrue(entry.getHabitId(), userId);
        if (habitOpt.isEmpty()) {
            log.warn("Habit not found for entry logging: {}", entry.getHabitId());
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Habit not found")));
        }

        Optional<HabitEntry> existingEntry = habitEntryRepository.findByHabitIdAndDate(userId, entry.getHabitId(), entry.getDate());
        if (existingEntry.isPresent()) {
            HabitEntry existing = existingEntry.get();
            if (entry.getValue() != null) existing.setValue(entry.getValue());
            if (entry.getIsCompleted() != null) existing.setIsCompleted(entry.getIsCompleted());
            if (entry.getMood() != null) existing.setMood(entry.getMood());
            if (entry.getNotes() != null) existing.setNotes(entry.getNotes());
            existing.setUpdatedBy(userId);

            HabitEntry updated = habitEntryRepository.save(existing);
            log.info("Habit entry updated for habit: {} on date: {}", entry.getHabitId(), entry.getDate());
            return ServiceResult.ok(updated);
        }

        entry.setUuid(UUID.randomUUID().toString());
        entry.setUserId(userId);
        entry.setCreatedBy(userId);
        entry.setActive(true);
        if (entry.getValue() == null) entry.setValue(0f);
        if (entry.getIsCompleted() == null) entry.setIsCompleted(false);

        HabitEntry saved = habitEntryRepository.save(entry);
        log.info("Habit entry created for habit: {} on date: {}", entry.getHabitId(), entry.getDate());
        return ServiceResult.ok(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<HabitEntry>> getHabitEntries(String habitUuid, int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching entries for habit: {} for user: {} page: {} size: {}", habitUuid, userId, page, size);

        Optional<Habit> habitOpt = habitRepository.findByUuidAndUserIdAndActiveTrue(habitUuid, userId);
        if (habitOpt.isEmpty()) {
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Habit not found")));
        }

        Page<HabitEntry> entryPage = habitEntryRepository.findByUserIdAndHabitIdAndActiveTrueOrderByDateDesc(userId, habitUuid, PageRequest.of(page, size));
        Pagination<HabitEntry> pagination = Pagination.of(entryPage.getContent(), page, size, entryPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Map<String, Object>> getHabitStats(String habitUuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching stats for habit: {} for user: {}", habitUuid, userId);

        Optional<Habit> habitOpt = habitRepository.findByUuidAndUserIdAndActiveTrue(habitUuid, userId);
        if (habitOpt.isEmpty()) {
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Habit not found")));
        }

        List<HabitEntry> entries = habitEntryRepository.findByUserIdAndHabitIdAndActiveTrueOrderByDateDesc(userId, habitUuid);
        long totalEntries = entries.size();
        long completedEntries = habitEntryRepository.countCompletedByHabitId(userId, habitUuid);

        int currentStreak = 0;
        int longestStreak = 0;
        int tempStreak = 0;
        for (HabitEntry e : entries) {
            if (Boolean.TRUE.equals(e.getIsCompleted())) {
                tempStreak++;
                longestStreak = Math.max(longestStreak, tempStreak);
            } else {
                tempStreak = 0;
            }
        }
        if (!entries.isEmpty() && Boolean.TRUE.equals(entries.get(0).getIsCompleted())) {
            for (HabitEntry e : entries) {
                if (Boolean.TRUE.equals(e.getIsCompleted())) {
                    currentStreak++;
                } else {
                    break;
                }
            }
        }

        double completionRate = totalEntries > 0 ? (double) completedEntries / totalEntries * 100 : 0;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalEntries", totalEntries);
        stats.put("completedEntries", completedEntries);
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        stats.put("currentStreak", currentStreak);
        stats.put("longestStreak", longestStreak);

        return ServiceResult.ok(stats);
    }
}
