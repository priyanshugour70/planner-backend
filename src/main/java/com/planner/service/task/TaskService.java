package com.planner.service.task;

import com.planner.dtos.ServiceResult;
import com.planner.entities.task.Subtask;
import com.planner.entities.task.Task;

import java.util.List;

public interface TaskService {

    ServiceResult<Task> createTask(Task task);

    ServiceResult<Task> updateTask(String uuid, Task task);

    ServiceResult<Void> deleteTask(String uuid);

    ServiceResult<Task> getTaskByUuid(String uuid);

    ServiceResult<List<Task>> getAllTasks();

    ServiceResult<List<Task>> getPendingTasks();

    ServiceResult<List<Task>> getCompletedTasks();

    ServiceResult<Task> completeTask(String uuid);

    ServiceResult<Task> addSubtask(String taskUuid, Subtask subtask);

    ServiceResult<Task> removeSubtask(String taskUuid, String subtaskUuid);

    ServiceResult<List<Task>> getTasksByDateRange(Long startDate, Long endDate);
}
