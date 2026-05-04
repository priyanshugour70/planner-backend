package com.planner.service.journal;

import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.journal.JournalEntry;
import com.planner.enums.JournalMood;

import java.util.List;
import java.util.Map;

public interface JournalService {

    ServiceResult<JournalEntry> createEntry(JournalEntry entry);

    ServiceResult<JournalEntry> updateEntry(String uuid, JournalEntry entry);

    ServiceResult<Void> deleteEntry(String uuid);

    ServiceResult<JournalEntry> getEntryByUuid(String uuid);

    ServiceResult<Pagination<JournalEntry>> getAllEntries(int page, int size);

    ServiceResult<Pagination<JournalEntry>> getEntriesByDateRange(Long startDate, Long endDate, int page, int size);

    ServiceResult<List<JournalEntry>> getEntriesByMood(JournalMood mood);

    ServiceResult<Map<String, Object>> getJournalStats();
}
