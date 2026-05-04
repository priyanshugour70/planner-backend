package com.planner.service.journal.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.journal.JournalEntry;
import com.planner.enums.JournalMood;
import com.planner.repositories.journal.JournalEntryRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.journal.JournalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JournalServiceImpl implements JournalService {

    private final JournalEntryRepository journalEntryRepository;

    @Override
    public ServiceResult<JournalEntry> createEntry(JournalEntry entry) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating journal entry for user: {}", userId);

        entry.setUuid(UUID.randomUUID().toString());
        entry.setUserId(userId);
        entry.setCreatedBy(userId);
        entry.setActive(true);

        if (entry.getMood() == null) entry.setMood(JournalMood.NEUTRAL);
        if (entry.getDate() == null) entry.setDate(System.currentTimeMillis());

        JournalEntry saved = journalEntryRepository.save(entry);
        log.info("Journal entry created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    public ServiceResult<JournalEntry> updateEntry(String uuid, JournalEntry entry) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating journal entry uuid: {} for user: {}", uuid, userId);

        return journalEntryRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (entry.getTitle() != null) existing.setTitle(entry.getTitle());
                    if (entry.getContent() != null) existing.setContent(entry.getContent());
                    if (entry.getMood() != null) existing.setMood(entry.getMood());
                    if (entry.getDate() != null) existing.setDate(entry.getDate());
                    if (entry.getTags() != null) existing.setTags(entry.getTags());
                    if (entry.getLinkedGoalIds() != null) existing.setLinkedGoalIds(entry.getLinkedGoalIds());
                    if (entry.getLinkedTaskIds() != null) existing.setLinkedTaskIds(entry.getLinkedTaskIds());
                    if (entry.getPhotos() != null) existing.setPhotos(entry.getPhotos());
                    if (entry.getGratitude() != null) existing.setGratitude(entry.getGratitude());
                    if (entry.getAchievements() != null) existing.setAchievements(entry.getAchievements());
                    if (entry.getChallenges() != null) existing.setChallenges(entry.getChallenges());
                    if (entry.getReflection() != null) existing.setReflection(entry.getReflection());
                    existing.setUpdatedBy(userId);

                    JournalEntry updated = journalEntryRepository.save(existing);
                    log.info("Journal entry updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Journal entry not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Journal entry not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteEntry(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting journal entry uuid: {} for user: {}", uuid, userId);

        return journalEntryRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    journalEntryRepository.save(existing);
                    log.info("Journal entry soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Journal entry not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Journal entry not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<JournalEntry> getEntryByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching journal entry uuid: {} for user: {}", uuid, userId);

        return journalEntryRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(ServiceResult::ok)
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Journal entry not found"))));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<JournalEntry>> getAllEntries() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all journal entries for user: {}", userId);

        List<JournalEntry> entries = journalEntryRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);
        return ServiceResult.ok(entries);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<JournalEntry>> getEntriesByDateRange(Long startDate, Long endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching journal entries by date range [{} - {}] for user: {}", startDate, endDate, userId);

        List<JournalEntry> entries = journalEntryRepository.findByDateRange(userId, startDate, endDate);
        return ServiceResult.ok(entries);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<JournalEntry>> getEntriesByMood(JournalMood mood) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching journal entries by mood: {} for user: {}", mood, userId);

        List<JournalEntry> entries = journalEntryRepository.findByMood(userId, mood);
        return ServiceResult.ok(entries);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Map<String, Object>> getJournalStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching journal stats for user: {}", userId);

        List<JournalEntry> allEntries = journalEntryRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);
        long totalEntries = journalEntryRepository.countByUserIdAndActiveTrue(userId);

        Map<JournalMood, Long> moodDistribution = allEntries.stream()
                .filter(e -> e.getMood() != null)
                .collect(Collectors.groupingBy(JournalEntry::getMood, Collectors.counting()));

        long entriesWithGratitude = allEntries.stream()
                .filter(e -> e.getGratitude() != null && !e.getGratitude().isEmpty())
                .count();

        long entriesWithReflection = allEntries.stream()
                .filter(e -> e.getReflection() != null && !e.getReflection().isEmpty())
                .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalEntries", totalEntries);
        stats.put("moodDistribution", moodDistribution);
        stats.put("entriesWithGratitude", entriesWithGratitude);
        stats.put("entriesWithReflection", entriesWithReflection);

        return ServiceResult.ok(stats);
    }
}
