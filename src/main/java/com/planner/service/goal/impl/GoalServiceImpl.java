package com.planner.service.goal.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.dtos.goal.*;
import com.planner.entities.goal.Goal;
import com.planner.entities.goal.Milestone;
import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;
import com.planner.repositories.goal.GoalRepository;
import com.planner.repositories.goal.MilestoneRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.goal.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;

    @Override
    public ServiceResult<GoalDTO> createGoal(GoalCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating goal for user: {}", userId);

        Goal goal = GoalMapper.toEntity(dto, userId);

        long count = goalRepository.countByUserIdAndActiveTrue(userId);
        goal.setNumber((int) count + 1);

        if (dto.getStartDate() != null) {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        Goal saved = goalRepository.save(goal);
        log.info("Goal created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(GoalMapper.toDTO(saved));
    }

    @Override
    public ServiceResult<GoalDTO> updateGoal(String uuid, GoalUpdateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating goal uuid: {} for user: {}", uuid, userId);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    GoalMapper.applyUpdate(existing, dto, userId);
                    Goal updated = goalRepository.save(existing);
                    log.info("Goal updated: {}", uuid);
                    return ServiceResult.ok(GoalMapper.toDTO(updated));
                })
                .orElseGet(() -> notFound("Goal not found"));
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
                .orElseGet(() -> notFound("Goal not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<GoalDTO> getGoalByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching goal uuid: {} for user: {}", uuid, userId);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(g -> ServiceResult.ok(GoalMapper.toDTO(g)))
                .orElseGet(() -> notFound("Goal not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<GoalDTO>> getAllGoals(int page, int size, String sortBy, String sortDir) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all goals for user: {} page: {} size: {} sort: {} {}", userId, page, size, sortBy, sortDir);

        Sort sort = buildSort(sortBy, sortDir);
        Page<Goal> goalPage = goalRepository.findByUserIdAndActiveTrueOrderByIsPinnedDescNumberAsc(userId, PageRequest.of(page, size));

        if (sortBy != null && !sortBy.isBlank()) {
            goalPage = goalRepository.findAll(
                    (root, query, cb) -> cb.and(
                            cb.equal(root.get("userId"), userId),
                            cb.isTrue(root.get("active"))
                    ),
                    PageRequest.of(page, size, sort)
            );
        }

        List<GoalDTO> dtos = goalPage.getContent().stream()
                .map(GoalMapper::toDTO)
                .toList();

        Pagination<GoalDTO> pagination = Pagination.of(dtos, page, size, goalPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<GoalDTO>> getGoalsByCategory(GoalCategory category) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching goals by category: {} for user: {}", category, userId);

        List<GoalDTO> goals = goalRepository.findByUserIdAndCategoryAndActiveTrue(userId, category)
                .stream().map(GoalMapper::toDTO).toList();
        return ServiceResult.ok(goals);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<GoalDTO>> getGoalsByStatus(GoalStatus status) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching goals by status: {} for user: {}", status, userId);

        List<GoalDTO> goals = goalRepository.findByUserIdAndStatusAndActiveTrue(userId, status)
                .stream().map(GoalMapper::toDTO).toList();
        return ServiceResult.ok(goals);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<GoalDTO>> getGoalsByPriority(GoalPriority priority) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GoalDTO> goals = goalRepository.findByUserIdAndPriorityAndActiveTrue(userId, priority)
                .stream().map(GoalMapper::toDTO).toList();
        return ServiceResult.ok(goals);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<GoalDTO>> getFavoriteGoals() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GoalDTO> goals = goalRepository.findByUserIdAndIsFavoriteTrueAndActiveTrue(userId)
                .stream().map(GoalMapper::toDTO).toList();
        return ServiceResult.ok(goals);
    }

    @Override
    public ServiceResult<GoalDTO> toggleFavorite(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        return goalRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(goal -> {
                    goal.setIsFavorite(!Boolean.TRUE.equals(goal.getIsFavorite()));
                    goal.setUpdatedBy(userId);
                    Goal saved = goalRepository.save(goal);
                    return ServiceResult.ok(GoalMapper.toDTO(saved));
                })
                .orElseGet(() -> notFound("Goal not found"));
    }

    @Override
    public ServiceResult<GoalDTO> togglePin(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        return goalRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(goal -> {
                    goal.setIsPinned(!Boolean.TRUE.equals(goal.getIsPinned()));
                    goal.setUpdatedBy(userId);
                    Goal saved = goalRepository.save(goal);
                    return ServiceResult.ok(GoalMapper.toDTO(saved));
                })
                .orElseGet(() -> notFound("Goal not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<GoalDTO>> searchGoals(String query, int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Searching goals for user: {} query: {}", userId, query);

        Page<Goal> goalPage = goalRepository.searchGoals(userId, query, PageRequest.of(page, size));
        List<GoalDTO> dtos = goalPage.getContent().stream()
                .map(GoalMapper::toDTO).toList();

        Pagination<GoalDTO> pagination = Pagination.of(dtos, page, size, goalPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<GoalStatsDTO> getGoalStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching goal stats for user: {}", userId);

        long total = goalRepository.countByUserIdAndActiveTrue(userId);
        long completed = goalRepository.countByUserIdAndActiveTrueAndStatus(userId, GoalStatus.COMPLETED);
        long inProgress = goalRepository.countByUserIdAndActiveTrueAndStatus(userId, GoalStatus.IN_PROGRESS);
        long notStarted = goalRepository.countByUserIdAndActiveTrueAndStatus(userId, GoalStatus.NOT_STARTED);
        long onHold = goalRepository.countByUserIdAndActiveTrueAndStatus(userId, GoalStatus.ON_HOLD);
        long abandoned = goalRepository.countByUserIdAndActiveTrueAndStatus(userId, GoalStatus.ABANDONED);
        long totalMilestones = milestoneRepository.countAllByUser(userId);
        long completedMilestones = milestoneRepository.countCompletedByUser(userId);
        Double avgProgress = goalRepository.averageProgressByUser(userId);
        long favorites = goalRepository.countByUserIdAndActiveTrueAndIsFavoriteTrue(userId);
        long overdue = goalRepository.findOverdueGoals(userId, System.currentTimeMillis()).size();

        Map<String, Long> byCategory = goalRepository.countByCategory(userId).stream()
                .collect(Collectors.toMap(
                        row -> ((GoalCategory) row[0]).name(),
                        row -> (Long) row[1]
                ));

        Map<String, Long> byPriority = goalRepository.countByPriority(userId).stream()
                .collect(Collectors.toMap(
                        row -> ((GoalPriority) row[0]).name(),
                        row -> (Long) row[1]
                ));

        GoalStatsDTO stats = GoalStatsDTO.builder()
                .totalGoals(total)
                .completedGoals(completed)
                .inProgressGoals(inProgress)
                .notStartedGoals(notStarted)
                .onHoldGoals(onHold)
                .abandonedGoals(abandoned)
                .totalMilestones(totalMilestones)
                .completedMilestones(completedMilestones)
                .averageProgress(avgProgress != null ? avgProgress : 0.0)
                .favoriteGoals(favorites)
                .overdueGoals(overdue)
                .goalsByCategory(byCategory)
                .goalsByPriority(byPriority)
                .build();

        return ServiceResult.ok(stats);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<GoalDTO>> getOverdueGoals() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GoalDTO> goals = goalRepository.findOverdueGoals(userId, System.currentTimeMillis())
                .stream().map(GoalMapper::toDTO).toList();
        return ServiceResult.ok(goals);
    }

    @Override
    public ServiceResult<Void> reorderGoals(GoalReorderDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Reordering goals for user: {}", userId);

        for (GoalReorderDTO.GoalOrderItem item : dto.getGoals()) {
            goalRepository.updateGoalNumber(item.getUuid(), userId, item.getNumber());
        }
        return ServiceResult.ok(null);
    }

    @Override
    public ServiceResult<GoalDTO> addMilestone(String goalUuid, MilestoneCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Adding milestone to goal: {} for user: {}", goalUuid, userId);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(goalUuid, userId)
                .map(goal -> {
                    Milestone m = GoalMapper.toMilestoneEntity(dto, userId);
                    int maxOrder = goal.getMilestones().stream()
                            .filter(ms -> Boolean.TRUE.equals(ms.getActive()))
                            .mapToInt(ms -> ms.getOrderIndex() != null ? ms.getOrderIndex() : 0)
                            .max().orElse(-1);
                    m.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : maxOrder + 1);
                    m.setGoal(goal);
                    goal.getMilestones().add(m);

                    Goal saved = goalRepository.save(goal);
                    recalculateGoalProgress(saved);
                    autoUpdateStatus(saved);

                    log.info("Milestone added to goal: {}", goalUuid);
                    return ServiceResult.ok(GoalMapper.toDTO(saved));
                })
                .orElseGet(() -> notFound("Goal not found"));
    }

    @Override
    public ServiceResult<GoalDTO> updateMilestone(String goalUuid, String milestoneUuid, MilestoneUpdateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating milestone uuid: {} in goal: {}", milestoneUuid, goalUuid);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(goalUuid, userId)
                .map(goal -> {
                    Milestone existing = goal.getMilestones().stream()
                            .filter(m -> m.getUuid().equals(milestoneUuid) && Boolean.TRUE.equals(m.getActive()))
                            .findFirst()
                            .orElse(null);

                    if (existing == null) {
                        return ServiceResult.<GoalDTO>fail(HttpStatus.NOT_FOUND,
                                List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Milestone not found in this goal")));
                    }

                    GoalMapper.applyMilestoneUpdate(existing, dto, userId);
                    milestoneRepository.save(existing);
                    recalculateGoalProgress(goal);
                    autoUpdateStatus(goal);

                    log.info("Milestone updated: {}", milestoneUuid);
                    return ServiceResult.ok(GoalMapper.toDTO(goal));
                })
                .orElseGet(() -> notFound("Goal not found"));
    }

    @Override
    public ServiceResult<GoalDTO> removeMilestone(String goalUuid, String milestoneUuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Removing milestone: {} from goal: {}", milestoneUuid, goalUuid);

        return goalRepository.findByUuidAndUserIdAndActiveTrue(goalUuid, userId)
                .map(goal -> {
                    Milestone toRemove = goal.getMilestones().stream()
                            .filter(m -> m.getUuid().equals(milestoneUuid))
                            .findFirst()
                            .orElse(null);

                    if (toRemove == null) {
                        return ServiceResult.<GoalDTO>fail(HttpStatus.NOT_FOUND,
                                List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Milestone not found in goal")));
                    }

                    toRemove.setActive(false);
                    toRemove.setUpdatedBy(userId);
                    milestoneRepository.save(toRemove);

                    recalculateGoalProgress(goal);
                    autoUpdateStatus(goal);

                    log.info("Milestone soft-deleted from goal: {}", goalUuid);
                    return ServiceResult.ok(GoalMapper.toDTO(goal));
                })
                .orElseGet(() -> notFound("Goal not found"));
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

    private void autoUpdateStatus(Goal goal) {
        float progress = goal.getProgress() != null ? goal.getProgress() : 0f;

        if (progress >= 1.0f && goal.getStatus() != GoalStatus.COMPLETED) {
            goal.setStatus(GoalStatus.COMPLETED);
            goal.setCompletedDate(System.currentTimeMillis());
            goalRepository.save(goal);
        } else if (progress > 0f && progress < 1.0f && goal.getStatus() == GoalStatus.NOT_STARTED) {
            goal.setStatus(GoalStatus.IN_PROGRESS);
            if (goal.getStartDate() == null) {
                goal.setStartDate(System.currentTimeMillis());
            }
            goalRepository.save(goal);
        }
    }

    private Sort buildSort(String sortBy, String sortDir) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "isPinned").and(Sort.by(Sort.Direction.ASC, "number"));
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(Sort.Direction.DESC, "isPinned").and(Sort.by(direction, sortBy));
    }

    private <T> ServiceResult<T> notFound(String message) {
        return ServiceResult.fail(HttpStatus.NOT_FOUND,
                List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, message)));
    }
}
