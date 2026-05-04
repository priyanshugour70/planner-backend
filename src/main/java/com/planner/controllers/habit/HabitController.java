package com.planner.controllers.habit;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.habit.Habit;
import com.planner.entities.habit.HabitEntry;
import com.planner.service.habit.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
@Tag(name = "Habits", description = "Habit tracking and management")
public class HabitController {

    private final HabitService habitService;

    @PostMapping
    @Operation(summary = "Create a new habit")
    public ResponseEntity<APIResponse<Habit>> create(@Valid @RequestBody Habit habit) {
        ServiceResult<Habit> result = habitService.createHabit(habit);
        return toApiResponse(result, "Habit created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all habits for the current user")
    public ResponseEntity<APIResponse<List<Habit>>> getAll() {
        ServiceResult<List<Habit>> result = habitService.getAllHabits();
        return toApiResponse(result, "Habits retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a habit by UUID")
    public ResponseEntity<APIResponse<Habit>> getByUuid(@PathVariable String uuid) {
        ServiceResult<Habit> result = habitService.getHabitByUuid(uuid);
        return toApiResponse(result, "Habit retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a habit")
    public ResponseEntity<APIResponse<Habit>> update(@PathVariable String uuid, @Valid @RequestBody Habit habit) {
        ServiceResult<Habit> result = habitService.updateHabit(uuid, habit);
        return toApiResponse(result, "Habit updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a habit")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable String uuid) {
        ServiceResult<Void> result = habitService.deleteHabit(uuid);
        return toApiResponse(result, "Habit deleted successfully");
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active habits")
    public ResponseEntity<APIResponse<List<Habit>>> getActive() {
        ServiceResult<List<Habit>> result = habitService.getActiveHabits();
        return toApiResponse(result, "Active habits retrieved successfully");
    }

    @PostMapping("/entries")
    @Operation(summary = "Log a habit entry")
    public ResponseEntity<APIResponse<HabitEntry>> logEntry(@Valid @RequestBody HabitEntry entry) {
        ServiceResult<HabitEntry> result = habitService.logHabitEntry(entry);
        return toApiResponse(result, "Habit entry logged successfully");
    }

    @GetMapping("/{habitUuid}/entries")
    @Operation(summary = "Get all entries for a habit")
    public ResponseEntity<APIResponse<List<HabitEntry>>> getEntries(@PathVariable String habitUuid) {
        ServiceResult<List<HabitEntry>> result = habitService.getHabitEntries(habitUuid);
        return toApiResponse(result, "Habit entries retrieved successfully");
    }

    @GetMapping("/{habitUuid}/stats")
    @Operation(summary = "Get statistics for a habit")
    public ResponseEntity<APIResponse<Map<String, Object>>> getStats(@PathVariable String habitUuid) {
        ServiceResult<Map<String, Object>> result = habitService.getHabitStats(habitUuid);
        return toApiResponse(result, "Habit stats retrieved successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
