package com.planner.repositories.habit;

import com.planner.entities.habit.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);

    Optional<Habit> findByUuidAndActiveTrue(String uuid);

    Optional<Habit> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT h FROM Habit h WHERE h.userId = :userId AND h.isActive = true AND h.active = true")
    List<Habit> findActiveHabits(@Param("userId") Long userId);

    @Query("SELECT h FROM Habit h WHERE h.userId = :userId AND h.goalId = :goalId AND h.active = true")
    List<Habit> findByGoalId(@Param("userId") Long userId, @Param("goalId") String goalId);

    long countByUserIdAndIsActiveTrueAndActiveTrue(Long userId);
}
