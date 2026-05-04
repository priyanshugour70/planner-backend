package com.planner.repositories.reminder;

import com.planner.entities.reminder.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUserIdAndActiveTrueOrderByReminderTimeAsc(Long userId);

    Optional<Reminder> findByUuidAndActiveTrue(String uuid);

    Optional<Reminder> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT r FROM Reminder r WHERE r.userId = :userId AND r.isCompleted = false AND r.isEnabled = true AND r.active = true ORDER BY r.reminderTime ASC")
    List<Reminder> findActiveReminders(@Param("userId") Long userId);

    @Query("SELECT r FROM Reminder r WHERE r.userId = :userId AND r.reminderTime BETWEEN :startTime AND :endTime AND r.active = true")
    List<Reminder> findByTimeRange(@Param("userId") Long userId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    long countByUserIdAndActiveTrue(Long userId);
}
