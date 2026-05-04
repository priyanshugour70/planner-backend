package com.planner.controllers.goal;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.goal.Goal;
import com.planner.entities.goal.Milestone;
import com.planner.enums.GoalCategory;
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
    public ResponseEntity<APIResponse<Goal>> create(@Valid @RequestBody Goal goal) {
        ServiceResult<Goal> result = goalService.createGoal(goal);
        return toApiResponse(result, "Goal created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all goals for the current user")
    public ResponseEntity<APIResponse<List<Goal>>> getAll() {
        ServiceResult<List<Goal>> result = goalService.getAllGoals();
        return toApiResponse(result, "Goals retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a goal by UUID")
    public ResponseEntity<APIResponse<Goal>> getByUuid(@PathVariable String uuid) {
        ServiceResult<Goal> result = goalService.getGoalByUuid(uuid);
        return toApiResponse(result, "Goal retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a goal")
    public ResponseEntity<APIResponse<Goal>> update(@PathVariable String uuid, @Valid @RequestBody Goal goal) {
        ServiceResult<Goal> result = goalService.updateGoal(uuid, goal);
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
    public ResponseEntity<APIResponse<List<Goal>>> getByCategory(@PathVariable GoalCategory category) {
        ServiceResult<List<Goal>> result = goalService.getGoalsByCategory(category);
        return toApiResponse(result, "Goals retrieved successfully");
    }

    @PostMapping("/{goalUuid}/milestones")
    @Operation(summary = "Add a milestone to a goal")
    public ResponseEntity<APIResponse<Goal>> addMilestone(
            @PathVariable String goalUuid, @Valid @RequestBody Milestone milestone) {
        ServiceResult<Goal> result = goalService.addMilestone(goalUuid, milestone);
        return toApiResponse(result, "Milestone added successfully");
    }

    @PutMapping("/{goalUuid}/milestones/{milestoneUuid}")
    @Operation(summary = "Update a milestone")
    public ResponseEntity<APIResponse<Goal>> updateMilestone(
            @PathVariable String goalUuid,
            @PathVariable String milestoneUuid,
            @Valid @RequestBody Milestone milestone) {
        ServiceResult<Goal> result = goalService.updateMilestone(goalUuid, milestoneUuid, milestone);
        return toApiResponse(result, "Milestone updated successfully");
    }

    @DeleteMapping("/{goalUuid}/milestones/{milestoneUuid}")
    @Operation(summary = "Remove a milestone from a goal")
    public ResponseEntity<APIResponse<Goal>> removeMilestone(
            @PathVariable String goalUuid, @PathVariable String milestoneUuid) {
        ServiceResult<Goal> result = goalService.removeMilestone(goalUuid, milestoneUuid);
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
