package com.planner.repositories.task;

import com.planner.entities.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);

    Optional<Task> findByUuidAndActiveTrue(String uuid);

    Optional<Task> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = false AND t.active = true ORDER BY t.dueDate ASC NULLS LAST")
    List<Task> findPendingTasks(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.isCompleted = true AND t.active = true ORDER BY t.completedAt DESC")
    List<Task> findCompletedTasks(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate AND t.active = true")
    List<Task> findTasksByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.linkedGoalId = :goalId AND t.active = true")
    List<Task> findByLinkedGoalId(@Param("userId") Long userId, @Param("goalId") String goalId);

    long countByUserIdAndIsCompletedTrueAndActiveTrue(Long userId);

    long countByUserIdAndActiveTrue(Long userId);
}
