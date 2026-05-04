package com.planner.repositories.task;

import com.planner.entities.task.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByUserIdAndActiveTrueOrderByDateAsc(Long userId);

    Optional<CalendarEvent> findByUuidAndActiveTrue(String uuid);

    Optional<CalendarEvent> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT e FROM CalendarEvent e WHERE e.userId = :userId AND e.date BETWEEN :startDate AND :endDate AND e.active = true ORDER BY e.date ASC")
    List<CalendarEvent> findByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate);
}
