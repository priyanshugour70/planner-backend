package com.planner.service.task;

import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.task.Subtask;
import com.planner.entities.task.Task;

import java.util.List;

public interface TaskService {

    ServiceResult<Task> createTask(Task task);

    ServiceResult<Task> updateTask(String uuid, Task task);

    ServiceResult<Void> deleteTask(String uuid);

    ServiceResult<Task> getTaskByUuid(String uuid);

    ServiceResult<Pagination<Task>> getAllTasks(int page, int size);

    ServiceResult<Pagination<Task>> getPendingTasks(int page, int size);

    ServiceResult<Pagination<Task>> getCompletedTasks(int page, int size);

    ServiceResult<Task> completeTask(String uuid);

    ServiceResult<Task> addSubtask(String taskUuid, Subtask subtask);

    ServiceResult<Task> removeSubtask(String taskUuid, String subtaskUuid);

    ServiceResult<Pagination<Task>> getTasksByDateRange(Long startDate, Long endDate, int page, int size);
}
