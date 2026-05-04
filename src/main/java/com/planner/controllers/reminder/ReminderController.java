package com.planner.controllers.reminder;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.reminder.Reminder;
import com.planner.service.reminder.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminders", description = "Reminder management")
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    @Operation(summary = "Create a new reminder")
    public ResponseEntity<APIResponse<Reminder>> create(@Valid @RequestBody Reminder reminder) {
        ServiceResult<Reminder> result = reminderService.createReminder(reminder);
        return toApiResponse(result, "Reminder created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all reminders")
    public ResponseEntity<APIResponse<List<Reminder>>> getAll() {
        ServiceResult<List<Reminder>> result = reminderService.getAllReminders();
        return toApiResponse(result, "Reminders retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a reminder by UUID")
    public ResponseEntity<APIResponse<Reminder>> getByUuid(@PathVariable String uuid) {
        ServiceResult<Reminder> result = reminderService.getReminderByUuid(uuid);
        return toApiResponse(result, "Reminder retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a reminder")
    public ResponseEntity<APIResponse<Reminder>> update(
            @PathVariable String uuid, @Valid @RequestBody Reminder reminder) {
        ServiceResult<Reminder> result = reminderService.updateReminder(uuid, reminder);
        return toApiResponse(result, "Reminder updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a reminder")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable String uuid) {
        ServiceResult<Void> result = reminderService.deleteReminder(uuid);
        return toApiResponse(result, "Reminder deleted successfully");
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active reminders")
    public ResponseEntity<APIResponse<List<Reminder>>> getActive() {
        ServiceResult<List<Reminder>> result = reminderService.getActiveReminders();
        return toApiResponse(result, "Active reminders retrieved successfully");
    }

    @PutMapping("/{uuid}/complete")
    @Operation(summary = "Mark a reminder as completed")
    public ResponseEntity<APIResponse<Reminder>> complete(@PathVariable String uuid) {
        ServiceResult<Reminder> result = reminderService.completeReminder(uuid);
        return toApiResponse(result, "Reminder marked as completed");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
