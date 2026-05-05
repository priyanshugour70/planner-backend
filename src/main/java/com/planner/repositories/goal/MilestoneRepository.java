package com.planner.repositories.goal;

import com.planner.entities.goal.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByGoalIdAndActiveTrue(Long goalId);

    List<Milestone> findByGoalIdAndActiveTrueOrderByOrderIndexAsc(Long goalId);

    Optional<Milestone> findByUuidAndActiveTrue(String uuid);

    @Query("SELECT m FROM Milestone m WHERE m.goal.userId = :userId AND m.isCompleted = true AND m.active = true")
    List<Milestone> findCompletedMilestonesByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.goal.id = :goalId AND m.isCompleted = true AND m.active = true")
    long countCompletedByGoalId(@Param("goalId") Long goalId);

    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.goal.id = :goalId AND m.active = true")
    long countByGoalId(@Param("goalId") Long goalId);

    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.goal.userId = :userId AND m.active = true")
    long countAllByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.goal.userId = :userId AND m.isCompleted = true AND m.active = true")
    long countCompletedByUser(@Param("userId") Long userId);

    @Query("SELECT m FROM Milestone m WHERE m.goal.userId = :userId AND m.active = true " +
            "AND m.targetDate IS NOT NULL AND m.targetDate < :now AND m.isCompleted = false")
    List<Milestone> findOverdueMilestonesByUser(@Param("userId") Long userId, @Param("now") Long now);
}
