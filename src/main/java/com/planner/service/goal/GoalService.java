package com.planner.service.goal;

import com.planner.dtos.ServiceResult;
import com.planner.entities.goal.Goal;
import com.planner.entities.goal.Milestone;
import com.planner.enums.GoalCategory;

import java.util.List;

public interface GoalService {

    ServiceResult<Goal> createGoal(Goal goal);

    ServiceResult<Goal> updateGoal(String uuid, Goal goal);

    ServiceResult<Void> deleteGoal(String uuid);

    ServiceResult<Goal> getGoalByUuid(String uuid);

    ServiceResult<List<Goal>> getAllGoals();

    ServiceResult<List<Goal>> getGoalsByCategory(GoalCategory category);

    ServiceResult<Goal> updateMilestone(String goalUuid, String milestoneUuid, Milestone milestone);

    ServiceResult<Goal> addMilestone(String goalUuid, Milestone milestone);

    ServiceResult<Goal> removeMilestone(String goalUuid, String milestoneUuid);
}
