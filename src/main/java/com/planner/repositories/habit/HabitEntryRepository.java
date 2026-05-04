package com.planner.repositories.habit;

import com.planner.entities.habit.HabitEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitEntryRepository extends JpaRepository<HabitEntry, Long> {

    Page<HabitEntry> findByUserIdAndHabitIdAndActiveTrueOrderByDateDesc(Long userId, String habitId, Pageable pageable);

    List<HabitEntry> findByUserIdAndHabitIdAndActiveTrueOrderByDateDesc(Long userId, String habitId);

    @Query("SELECT he FROM HabitEntry he WHERE he.userId = :userId AND he.habitId = :habitId AND he.date = :date AND he.active = true")
    Optional<HabitEntry> findByHabitIdAndDate(@Param("userId") Long userId, @Param("habitId") String habitId, @Param("date") Long date);

    @Query("SELECT COUNT(he) FROM HabitEntry he WHERE he.userId = :userId AND he.habitId = :habitId AND he.active = true AND he.isCompleted = true")
    long countCompletedByHabitId(@Param("userId") Long userId, @Param("habitId") String habitId);

    long countByUserIdAndHabitIdAndActiveTrue(Long userId, String habitId);

    List<HabitEntry> findByUserIdAndActiveTrueOrderByDateDesc(Long userId);
}
