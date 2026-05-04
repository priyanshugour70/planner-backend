package com.planner.repositories.habit;

import com.planner.entities.habit.HabitEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitEntryRepository extends JpaRepository<HabitEntry, Long> {

    List<HabitEntry> findByUserIdAndHabitIdAndActiveTrueOrderByDateDesc(Long userId, String habitId);

    Optional<HabitEntry> findByUuidAndActiveTrue(String uuid);

    @Query("SELECT he FROM HabitEntry he WHERE he.userId = :userId AND he.date BETWEEN :startDate AND :endDate AND he.active = true")
    List<HabitEntry> findByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    @Query("SELECT he FROM HabitEntry he WHERE he.userId = :userId AND he.habitId = :habitId AND he.date = :date AND he.active = true")
    Optional<HabitEntry> findByHabitIdAndDate(@Param("userId") Long userId, @Param("habitId") String habitId, @Param("date") Long date);

    @Query("SELECT COUNT(he) FROM HabitEntry he WHERE he.userId = :userId AND he.habitId = :habitId AND he.isCompleted = true AND he.active = true")
    long countCompletedByHabitId(@Param("userId") Long userId, @Param("habitId") String habitId);

    List<HabitEntry> findByUserIdAndActiveTrueOrderByDateDesc(Long userId);
}
