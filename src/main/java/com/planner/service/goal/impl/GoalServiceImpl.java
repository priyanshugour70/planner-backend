package com.planner.service.goal.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.goal.Goal;
import com.planner.entities.goal.Milestone;
import com.planner.enums.GoalCategory;
import com.planner.repositories.goal.GoalRepository;
import com.planner.repositories.goal.MilestoneRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.goal.GoalService;
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
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;

    @Override
    public ServiceResult<Goal> createGoal(Goal goal) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating goal for user: {}", userId);

        goal.setUuid(UUID.randomUUID().toString());
        goal.setUserId(userId);
        goal.setCreatedBy(userId);
        goal.setActive(true);

        if (goal.getProgress() == null) {
            goal.setProgress(0f);
        }

        long count = goalRepository.countByUserIdAndActiveTrue(userId);
        goal.setNumber((int) count + 1);

        Goal saved = goalRepository.save(goal);
        log.info("Goal created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    public ServiceResult<Goal> updateGoal(String uuid, Goal goal) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating goal uuid: {} for user: {}", uuid, userId);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (goal.getTitle() != null) existing.setTitle(goal.getTitle());
                    if (goal.getDescription() != null) existing.setDescription(goal.getDescription());
                    if (goal.getCategory() != null) existing.setCategory(goal.getCategory());
                    if (goal.getIcon() != null) existing.setIcon(goal.getIcon());
                    if (goal.getColor() != null) existing.setColor(goal.getColor());
                    if (goal.getProgress() != null) existing.setProgress(goal.getProgress());
                    if (goal.getTargetDate() != null) existing.setTargetDate(goal.getTargetDate());
                    if (goal.getNumber() != null) existing.setNumber(goal.getNumber());
                    existing.setUpdatedBy(userId);

                    Goal updated = goalRepository.save(existing);
                    log.info("Goal updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Goal not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Goal not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteGoal(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting goal uuid: {} for user: {}", uuid, userId);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    existing.getMilestones().forEach(m -> m.setActive(false));
                    goalRepository.save(existing);
                    log.info("Goal soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Goal not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Goal not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Goal> getGoalByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching goal uuid: {} for user: {}", uuid, userId);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(ServiceResult::ok)
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Goal not found"))));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Goal>> getAllGoals() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all goals for user: {}", userId);

        List<Goal> goals = goalRepository.findByUserIdAndActiveTrueOrderByNumberAsc(userId);
        return ServiceResult.ok(goals);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Goal>> getGoalsByCategory(GoalCategory category) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching goals by category: {} for user: {}", category, userId);

        List<Goal> goals = goalRepository.findByUserIdAndCategoryAndActiveTrue(userId, category);
        return ServiceResult.ok(goals);
    }

    @Override
    public ServiceResult<Goal> updateMilestone(String goalUuid, String milestoneUuid, Milestone milestone) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating milestone uuid: {} in goal: {}", milestoneUuid, goalUuid);

        return milestoneRepository.findByUuidAndActiveTrue(milestoneUuid)
                .<ServiceResult<Goal>>map(existing -> {
                    if (!existing.getGoal().getUserId().equals(userId)) {
                        log.warn("Unauthorized milestone update attempt by user: {}", userId);
                        return ServiceResult.<Goal>fail(HttpStatus.FORBIDDEN,
                                List.of(ErrorResponse.of(HttpStatus.FORBIDDEN, "Not authorized to update this milestone")));
                    }

                    if (milestone.getTitle() != null) existing.setTitle(milestone.getTitle());
                    if (milestone.getDescription() != null) existing.setDescription(milestone.getDescription());
                    if (milestone.getIsCompleted() != null) {
                        existing.setIsCompleted(milestone.getIsCompleted());
                        if (milestone.getIsCompleted()) {
                            existing.setCompletedAt(System.currentTimeMillis());
                        }
                    }
                    if (milestone.getTargetDate() != null) existing.setTargetDate(milestone.getTargetDate());
                    if (milestone.getQuality() != null) existing.setQuality(milestone.getQuality());
                    if (milestone.getRating() != null) existing.setRating(milestone.getRating());
                    existing.setUpdatedBy(userId);

                    milestoneRepository.save(existing);
                    Goal goal = existing.getGoal();
                    recalculateGoalProgress(goal);
                    log.info("Milestone updated: {}", milestoneUuid);
                    return ServiceResult.ok(goal);
                })
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Milestone not found"))));
    }

    @Override
    public ServiceResult<Goal> addMilestone(String goalUuid, Milestone milestone) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Adding milestone to goal: {} for user: {}", goalUuid, userId);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(goalUuid, userId)
                .map(goal -> {
                    milestone.setUuid(UUID.randomUUID().toString());
                    milestone.setGoal(goal);
                    milestone.setCreatedBy(userId);
                    milestone.setActive(true);
                    if (milestone.getIsCompleted() == null) {
                        milestone.setIsCompleted(false);
                    }

                    goal.getMilestones().add(milestone);
                    Goal saved = goalRepository.save(goal);
                    recalculateGoalProgress(saved);
                    log.info("Milestone added to goal: {}", goalUuid);
                    return ServiceResult.ok(saved);
                })
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Goal not found"))));
    }

    @Override
    public ServiceResult<Goal> removeMilestone(String goalUuid, String milestoneUuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Removing milestone: {} from goal: {}", milestoneUuid, goalUuid);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(goalUuid, userId)
                .map(goal -> {
                    boolean removed = goal.getMilestones().removeIf(m -> m.getUuid().equals(milestoneUuid));
                    if (!removed) {
                        return ServiceResult.<Goal>fail(HttpStatus.NOT_FOUND,
                                List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Milestone not found in goal")));
                    }
                    Goal saved = goalRepository.save(goal);
                    recalculateGoalProgress(saved);
                    log.info("Milestone removed from goal: {}", goalUuid);
                    return ServiceResult.ok(saved);
                })
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Goal not found"))));
    }

    private void recalculateGoalProgress(Goal goal) {
        long total = milestoneRepository.countByGoalId(goal.getId());
        if (total == 0) {
            goal.setProgress(0f);
        } else {
            long completed = milestoneRepository.countCompletedByGoalId(goal.getId());
            goal.setProgress((float) completed / total);
        }
        goalRepository.save(goal);
    }
}
