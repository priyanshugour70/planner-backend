package com.planner.repositories.habit;

import com.planner.entities.habit.Habit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    Page<Habit> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Habit> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);

    Optional<Habit> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT h FROM Habit h WHERE h.userId = :userId AND h.active = true AND h.isActive = true ORDER BY h.createdAt DESC")
    List<Habit> findActiveHabits(@Param("userId") Long userId);

    long countByUserIdAndActiveTrue(Long userId);

    long countByUserIdAndIsActiveTrueAndActiveTrue(Long userId);
}
