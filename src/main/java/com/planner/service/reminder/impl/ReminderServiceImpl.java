package com.planner.service.reminder.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.reminder.Reminder;
import com.planner.repositories.reminder.ReminderRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.reminder.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public ServiceResult<Reminder> createReminder(Reminder reminder) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating reminder for user: {}", userId);

        reminder.setUuid(UUID.randomUUID().toString());
        reminder.setUserId(userId);
        reminder.setCreatedBy(userId);
        reminder.setActive(true);

        if (reminder.getIsEnabled() == null) reminder.setIsEnabled(true);
        if (reminder.getIsCompleted() == null) reminder.setIsCompleted(false);

        Reminder saved = reminderRepository.save(reminder);
        log.info("Reminder created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    public ServiceResult<Reminder> updateReminder(String uuid, Reminder reminder) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating reminder uuid: {} for user: {}", uuid, userId);

        return reminderRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (reminder.getTitle() != null) existing.setTitle(reminder.getTitle());
                    if (reminder.getDescription() != null) existing.setDescription(reminder.getDescription());
                    if (reminder.getReminderTime() != null) existing.setReminderTime(reminder.getReminderTime());
                    if (reminder.getRepeatType() != null) existing.setRepeatType(reminder.getRepeatType());
                    if (reminder.getPriority() != null) existing.setPriority(reminder.getPriority());
                    if (reminder.getIsEnabled() != null) existing.setIsEnabled(reminder.getIsEnabled());
                    if (reminder.getIsCompleted() != null) existing.setIsCompleted(reminder.getIsCompleted());
                    if (reminder.getLinkedNoteId() != null) existing.setLinkedNoteId(reminder.getLinkedNoteId());
                    if (reminder.getLinkedTaskId() != null) existing.setLinkedTaskId(reminder.getLinkedTaskId());
                    if (reminder.getLinkedGoalId() != null) existing.setLinkedGoalId(reminder.getLinkedGoalId());
                    if (reminder.getColor() != null) existing.setColor(reminder.getColor());
                    if (reminder.getNotificationId() != null) existing.setNotificationId(reminder.getNotificationId());
                    existing.setUpdatedBy(userId);

                    Reminder updated = reminderRepository.save(existing);
                    log.info("Reminder updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Reminder not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Reminder not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteReminder(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting reminder uuid: {} for user: {}", uuid, userId);

        return reminderRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    reminderRepository.save(existing);
                    log.info("Reminder soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Reminder not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Reminder not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Reminder> getReminderByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching reminder uuid: {} for user: {}", uuid, userId);

        return reminderRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(ServiceResult::ok)
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Reminder not found"))));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<Reminder>> getAllReminders(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all reminders for user: {} page: {} size: {}", userId, page, size);

        Page<Reminder> reminderPage = reminderRepository.findByUserIdAndActiveTrueOrderByReminderTimeAsc(userId, PageRequest.of(page, size));
        Pagination<Reminder> pagination = Pagination.of(reminderPage.getContent(), page, size, reminderPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Reminder>> getActiveReminders() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching active reminders for user: {}", userId);

        List<Reminder> reminders = reminderRepository.findActiveReminders(userId);
        return ServiceResult.ok(reminders);
    }

    @Override
    public ServiceResult<Reminder> completeReminder(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Completing reminder uuid: {} for user: {}", uuid, userId);

        return reminderRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setIsCompleted(true);
                    existing.setUpdatedBy(userId);
                    Reminder updated = reminderRepository.save(existing);
                    log.info("Reminder completed: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Reminder not found for completion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Reminder not found")));
                });
    }
}
