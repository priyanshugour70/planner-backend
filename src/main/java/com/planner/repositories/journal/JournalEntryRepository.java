package com.planner.repositories.journal;

import com.planner.entities.journal.JournalEntry;
import com.planner.enums.JournalMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByUserIdAndActiveTrueOrderByDateDesc(Long userId);

    Optional<JournalEntry> findByUuidAndActiveTrue(String uuid);

    Optional<JournalEntry> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT j FROM JournalEntry j WHERE j.userId = :userId AND j.date BETWEEN :startDate AND :endDate AND j.active = true ORDER BY j.date DESC")
    List<JournalEntry> findByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    @Query("SELECT j FROM JournalEntry j WHERE j.userId = :userId AND j.mood = :mood AND j.active = true ORDER BY j.date DESC")
    List<JournalEntry> findByMood(@Param("userId") Long userId, @Param("mood") JournalMood mood);

    long countByUserIdAndActiveTrue(Long userId);
}
