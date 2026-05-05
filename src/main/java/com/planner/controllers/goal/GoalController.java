package com.planner.controllers.goal;

import com.planner.dtos.APIResponse;
import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.dtos.goal.*;
import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;
import com.planner.service.goal.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Goal management")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new goal")
    public ResponseEntity<APIResponse<GoalDTO>> create(@Valid @RequestBody GoalCreateDTO dto) {
        ServiceResult<GoalDTO> result = goalService.createGoal(dto);
        return toApiResponse(result, "Goal created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all goals with pagination and sorting")
    public ResponseEntity<APIResponse<Pagination<GoalDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        ServiceResult<Pagination<GoalDTO>> result = goalService.getAllGoals(page, size, sortBy, sortDir);
        return toApiResponse(result, "Goals retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a goal by UUID")
    public ResponseEntity<APIResponse<GoalDTO>> getByUuid(@PathVariable String uuid) {
        ServiceResult<GoalDTO> result = goalService.getGoalByUuid(uuid);
        return toApiResponse(result, "Goal retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a goal")
    public ResponseEntity<APIResponse<GoalDTO>> update(@PathVariable String uuid, @Valid @RequestBody GoalUpdateDTO dto) {
        ServiceResult<GoalDTO> result = goalService.updateGoal(uuid, dto);
        return toApiResponse(result, "Goal updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a goal")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable String uuid) {
        ServiceResult<Void> result = goalService.deleteGoal(uuid);
        return toApiResponse(result, "Goal deleted successfully");
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get goals by category")
    public ResponseEntity<APIResponse<List<GoalDTO>>> getByCategory(@PathVariable GoalCategory category) {
        ServiceResult<List<GoalDTO>> result = goalService.getGoalsByCategory(category);
        return toApiResponse(result, "Goals retrieved successfully");
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get goals by status")
    public ResponseEntity<APIResponse<List<GoalDTO>>> getByStatus(@PathVariable GoalStatus status) {
        ServiceResult<List<GoalDTO>> result = goalService.getGoalsByStatus(status);
        return toApiResponse(result, "Goals retrieved successfully");
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get goals by priority")
    public ResponseEntity<APIResponse<List<GoalDTO>>> getByPriority(@PathVariable GoalPriority priority) {
        ServiceResult<List<GoalDTO>> result = goalService.getGoalsByPriority(priority);
        return toApiResponse(result, "Goals retrieved successfully");
    }

    @GetMapping("/favorites")
    @Operation(summary = "Get favorite goals")
    public ResponseEntity<APIResponse<List<GoalDTO>>> getFavorites() {
        ServiceResult<List<GoalDTO>> result = goalService.getFavoriteGoals();
        return toApiResponse(result, "Favorite goals retrieved successfully");
    }

    @PatchMapping("/{uuid}/favorite")
    @Operation(summary = "Toggle goal as favorite")
    public ResponseEntity<APIResponse<GoalDTO>> toggleFavorite(@PathVariable String uuid) {
        ServiceResult<GoalDTO> result = goalService.toggleFavorite(uuid);
        return toApiResponse(result, "Goal favorite toggled");
    }

    @PatchMapping("/{uuid}/pin")
    @Operation(summary = "Toggle goal as pinned")
    public ResponseEntity<APIResponse<GoalDTO>> togglePin(@PathVariable String uuid) {
        ServiceResult<GoalDTO> result = goalService.togglePin(uuid);
        return toApiResponse(result, "Goal pin toggled");
    }

    @GetMapping("/search")
    @Operation(summary = "Search goals by title, description, or tags")
    public ResponseEntity<APIResponse<Pagination<GoalDTO>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ServiceResult<Pagination<GoalDTO>> result = goalService.searchGoals(q, page, size);
        return toApiResponse(result, "Search results retrieved");
    }

    @GetMapping("/stats")
    @Operation(summary = "Get goal statistics")
    public ResponseEntity<APIResponse<GoalStatsDTO>> getStats() {
        ServiceResult<GoalStatsDTO> result = goalService.getGoalStats();
        return toApiResponse(result, "Goal stats retrieved");
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue goals")
    public ResponseEntity<APIResponse<List<GoalDTO>>> getOverdue() {
        ServiceResult<List<GoalDTO>> result = goalService.getOverdueGoals();
        return toApiResponse(result, "Overdue goals retrieved");
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reorder goals")
    public ResponseEntity<APIResponse<Void>> reorder(@Valid @RequestBody GoalReorderDTO dto) {
        ServiceResult<Void> result = goalService.reorderGoals(dto);
        return toApiResponse(result, "Goals reordered successfully");
    }

    @PostMapping("/{goalUuid}/milestones")
    @Operation(summary = "Add a milestone to a goal")
    public ResponseEntity<APIResponse<GoalDTO>> addMilestone(
            @PathVariable String goalUuid, @Valid @RequestBody MilestoneCreateDTO dto) {
        ServiceResult<GoalDTO> result = goalService.addMilestone(goalUuid, dto);
        return toApiResponse(result, "Milestone added successfully");
    }

    @PutMapping("/{goalUuid}/milestones/{milestoneUuid}")
    @Operation(summary = "Update a milestone")
    public ResponseEntity<APIResponse<GoalDTO>> updateMilestone(
            @PathVariable String goalUuid,
            @PathVariable String milestoneUuid,
            @Valid @RequestBody MilestoneUpdateDTO dto) {
        ServiceResult<GoalDTO> result = goalService.updateMilestone(goalUuid, milestoneUuid, dto);
        return toApiResponse(result, "Milestone updated successfully");
    }

    @DeleteMapping("/{goalUuid}/milestones/{milestoneUuid}")
    @Operation(summary = "Remove a milestone from a goal")
    public ResponseEntity<APIResponse<GoalDTO>> removeMilestone(
            @PathVariable String goalUuid, @PathVariable String milestoneUuid) {
        ServiceResult<GoalDTO> result = goalService.removeMilestone(goalUuid, milestoneUuid);
        return toApiResponse(result, "Milestone removed successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
