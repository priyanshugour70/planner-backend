package com.planner.controllers.task;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.task.Subtask;
import com.planner.entities.task.Task;
import com.planner.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<APIResponse<Task>> create(@Valid @RequestBody Task task) {
        ServiceResult<Task> result = taskService.createTask(task);
        return toApiResponse(result, "Task created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<APIResponse<List<Task>>> getAll() {
        ServiceResult<List<Task>> result = taskService.getAllTasks();
        return toApiResponse(result, "Tasks retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a task by UUID")
    public ResponseEntity<APIResponse<Task>> getByUuid(@PathVariable String uuid) {
        ServiceResult<Task> result = taskService.getTaskByUuid(uuid);
        return toApiResponse(result, "Task retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a task")
    public ResponseEntity<APIResponse<Task>> update(@PathVariable String uuid, @Valid @RequestBody Task task) {
        ServiceResult<Task> result = taskService.updateTask(uuid, task);
        return toApiResponse(result, "Task updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable String uuid) {
        ServiceResult<Void> result = taskService.deleteTask(uuid);
        return toApiResponse(result, "Task deleted successfully");
    }

    @PutMapping("/{uuid}/complete")
    @Operation(summary = "Mark a task as completed")
    public ResponseEntity<APIResponse<Task>> complete(@PathVariable String uuid) {
        ServiceResult<Task> result = taskService.completeTask(uuid);
        return toApiResponse(result, "Task marked as completed");
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending tasks")
    public ResponseEntity<APIResponse<List<Task>>> getPending() {
        ServiceResult<List<Task>> result = taskService.getPendingTasks();
        return toApiResponse(result, "Pending tasks retrieved successfully");
    }

    @GetMapping("/completed")
    @Operation(summary = "Get all completed tasks")
    public ResponseEntity<APIResponse<List<Task>>> getCompleted() {
        ServiceResult<List<Task>> result = taskService.getCompletedTasks();
        return toApiResponse(result, "Completed tasks retrieved successfully");
    }

    @PostMapping("/{taskUuid}/subtasks")
    @Operation(summary = "Add a subtask to a task")
    public ResponseEntity<APIResponse<Task>> addSubtask(
            @PathVariable String taskUuid, @Valid @RequestBody Subtask subtask) {
        ServiceResult<Task> result = taskService.addSubtask(taskUuid, subtask);
        return toApiResponse(result, "Subtask added successfully");
    }

    @DeleteMapping("/{taskUuid}/subtasks/{subtaskUuid}")
    @Operation(summary = "Remove a subtask")
    public ResponseEntity<APIResponse<Task>> removeSubtask(
            @PathVariable String taskUuid, @PathVariable String subtaskUuid) {
        ServiceResult<Task> result = taskService.removeSubtask(taskUuid, subtaskUuid);
        return toApiResponse(result, "Subtask removed successfully");
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get tasks within a date range")
    public ResponseEntity<APIResponse<List<Task>>> getByDateRange(
            @RequestParam Long startDate, @RequestParam Long endDate) {
        ServiceResult<List<Task>> result = taskService.getTasksByDateRange(startDate, endDate);
        return toApiResponse(result, "Tasks retrieved successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
