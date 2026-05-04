package com.planner.service.task.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.task.Subtask;
import com.planner.entities.task.Task;
import com.planner.repositories.task.TaskRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public ServiceResult<Task> createTask(Task task) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating task for user: {}", userId);

        task.setUuid(UUID.randomUUID().toString());
        task.setUserId(userId);
        task.setCreatedBy(userId);
        task.setActive(true);

        if (task.getIsCompleted() == null) {
            task.setIsCompleted(false);
        }

        if (task.getSubtasks() != null) {
            task.getSubtasks().forEach(subtask -> {
                subtask.setUuid(UUID.randomUUID().toString());
                subtask.setTask(task);
                subtask.setActive(true);
                if (subtask.getIsCompleted() == null) {
                    subtask.setIsCompleted(false);
                }
            });
        }

        Task saved = taskRepository.save(task);
        log.info("Task created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    public ServiceResult<Task> updateTask(String uuid, Task task) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating task uuid: {} for user: {}", uuid, userId);

        return taskRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (task.getTitle() != null) existing.setTitle(task.getTitle());
                    if (task.getDescription() != null) existing.setDescription(task.getDescription());
                    if (task.getIsCompleted() != null) {
                        existing.setIsCompleted(task.getIsCompleted());
                        if (task.getIsCompleted()) {
                            existing.setCompletedAt(System.currentTimeMillis());
                        }
                    }
                    if (task.getPriority() != null) existing.setPriority(task.getPriority());
                    if (task.getItemPriority() != null) existing.setItemPriority(task.getItemPriority());
                    if (task.getDueDate() != null) existing.setDueDate(task.getDueDate());
                    if (task.getLinkedGoalId() != null) existing.setLinkedGoalId(task.getLinkedGoalId());
                    if (task.getLinkedNoteId() != null) existing.setLinkedNoteId(task.getLinkedNoteId());
                    if (task.getLinkedReminderId() != null) existing.setLinkedReminderId(task.getLinkedReminderId());
                    if (task.getTags() != null) existing.setTags(task.getTags());
                    if (task.getRepeatType() != null) existing.setRepeatType(task.getRepeatType());
                    if (task.getReminder() != null) existing.setReminder(task.getReminder());
                    if (task.getReminderEnabled() != null) existing.setReminderEnabled(task.getReminderEnabled());
                    if (task.getNotificationId() != null) existing.setNotificationId(task.getNotificationId());
                    existing.setUpdatedBy(userId);

                    Task updated = taskRepository.save(existing);
                    log.info("Task updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Task not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Task not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteTask(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting task uuid: {} for user: {}", uuid, userId);

        return taskRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    existing.getSubtasks().forEach(s -> s.setActive(false));
                    taskRepository.save(existing);
                    log.info("Task soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Task not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Task not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Task> getTaskByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching task uuid: {} for user: {}", uuid, userId);

        return taskRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(ServiceResult::ok)
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Task not found"))));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Task>> getAllTasks() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all tasks for user: {}", userId);

        List<Task> tasks = taskRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
        return ServiceResult.ok(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Task>> getPendingTasks() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching pending tasks for user: {}", userId);

        List<Task> tasks = taskRepository.findPendingTasks(userId);
        return ServiceResult.ok(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Task>> getCompletedTasks() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching completed tasks for user: {}", userId);

        List<Task> tasks = taskRepository.findCompletedTasks(userId);
        return ServiceResult.ok(tasks);
    }

    @Override
    public ServiceResult<Task> completeTask(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Completing task uuid: {} for user: {}", uuid, userId);

        return taskRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setIsCompleted(true);
                    existing.setCompletedAt(System.currentTimeMillis());
                    existing.setUpdatedBy(userId);

                    existing.getSubtasks().forEach(subtask -> {
                        if (!subtask.getIsCompleted()) {
                            subtask.setIsCompleted(true);
                            subtask.setCompletedAt(System.currentTimeMillis());
                        }
                    });

                    Task updated = taskRepository.save(existing);
                    log.info("Task completed: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Task not found"))));
    }

    @Override
    public ServiceResult<Task> addSubtask(String taskUuid, Subtask subtask) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Adding subtask to task: {} for user: {}", taskUuid, userId);

        return taskRepository.findByUuidAndUserIdAndActiveTrue(taskUuid, userId)
                .map(task -> {
                    subtask.setUuid(UUID.randomUUID().toString());
                    subtask.setTask(task);
                    subtask.setCreatedBy(userId);
                    subtask.setActive(true);
                    if (subtask.getIsCompleted() == null) {
                        subtask.setIsCompleted(false);
                    }

                    task.getSubtasks().add(subtask);
                    Task saved = taskRepository.save(task);
                    log.info("Subtask added to task: {}", taskUuid);
                    return ServiceResult.ok(saved);
                })
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Task not found"))));
    }

    @Override
    public ServiceResult<Task> removeSubtask(String taskUuid, String subtaskUuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Removing subtask: {} from task: {}", subtaskUuid, taskUuid);

        return taskRepository.findByUuidAndUserIdAndActiveTrue(taskUuid, userId)
                .map(task -> {
                    boolean removed = task.getSubtasks().removeIf(s -> s.getUuid().equals(subtaskUuid));
                    if (!removed) {
                        return ServiceResult.<Task>fail(HttpStatus.NOT_FOUND,
                                List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Subtask not found in task")));
                    }
                    Task saved = taskRepository.save(task);
                    log.info("Subtask removed from task: {}", taskUuid);
                    return ServiceResult.ok(saved);
                })
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Task not found"))));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Task>> getTasksByDateRange(Long startDate, Long endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching tasks by date range [{} - {}] for user: {}", startDate, endDate, userId);

        List<Task> tasks = taskRepository.findTasksByDateRange(userId, startDate, endDate);
        return ServiceResult.ok(tasks);
    }
}
