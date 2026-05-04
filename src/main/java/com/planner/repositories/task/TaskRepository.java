package com.planner.repositories.task;

import com.planner.entities.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Task> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);

    Optional<Task> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.active = true AND t.isCompleted = false ORDER BY t.dueDate ASC")
    Page<Task> findPendingTasks(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.active = true AND t.isCompleted = false ORDER BY t.dueDate ASC")
    List<Task> findPendingTasks(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.active = true AND t.isCompleted = true ORDER BY t.completedAt DESC")
    Page<Task> findCompletedTasks(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.active = true AND t.isCompleted = true ORDER BY t.completedAt DESC")
    List<Task> findCompletedTasks(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.active = true AND t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    Page<Task> findTasksByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.active = true AND t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    List<Task> findTasksByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    long countByUserIdAndActiveTrue(Long userId);

    long countByUserIdAndActiveTrueAndIsCompletedTrue(Long userId);

    long countByUserIdAndIsCompletedTrueAndActiveTrue(Long userId);
}
