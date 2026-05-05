package com.planner.service.goal;

import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.dtos.goal.*;
import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;

import java.util.List;

public interface GoalService {

    ServiceResult<GoalDTO> createGoal(GoalCreateDTO dto);

    ServiceResult<GoalDTO> updateGoal(String uuid, GoalUpdateDTO dto);

    ServiceResult<Void> deleteGoal(String uuid);

    ServiceResult<GoalDTO> getGoalByUuid(String uuid);

    ServiceResult<Pagination<GoalDTO>> getAllGoals(int page, int size, String sortBy, String sortDir);

    ServiceResult<List<GoalDTO>> getGoalsByCategory(GoalCategory category);

    ServiceResult<List<GoalDTO>> getGoalsByStatus(GoalStatus status);

    ServiceResult<List<GoalDTO>> getGoalsByPriority(GoalPriority priority);

    ServiceResult<List<GoalDTO>> getFavoriteGoals();

    ServiceResult<GoalDTO> toggleFavorite(String uuid);

    ServiceResult<GoalDTO> togglePin(String uuid);

    ServiceResult<Pagination<GoalDTO>> searchGoals(String query, int page, int size);

    ServiceResult<GoalStatsDTO> getGoalStats();

    ServiceResult<List<GoalDTO>> getOverdueGoals();

    ServiceResult<Void> reorderGoals(GoalReorderDTO dto);

    ServiceResult<GoalDTO> addMilestone(String goalUuid, MilestoneCreateDTO dto);

    ServiceResult<GoalDTO> updateMilestone(String goalUuid, String milestoneUuid, MilestoneUpdateDTO dto);

    ServiceResult<GoalDTO> removeMilestone(String goalUuid, String milestoneUuid);
}
