package com.planner.controllers.journal;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.journal.JournalEntry;
import com.planner.enums.JournalMood;
import com.planner.service.journal.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/journals")
@RequiredArgsConstructor
@Tag(name = "Journals", description = "Journal entry management")
public class JournalController {

    private final JournalService journalService;

    @PostMapping
    @Operation(summary = "Create a new journal entry")
    public ResponseEntity<APIResponse<JournalEntry>> create(@Valid @RequestBody JournalEntry entry) {
        ServiceResult<JournalEntry> result = journalService.createEntry(entry);
        return toApiResponse(result, "Journal entry created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all journal entries for the current user")
    public ResponseEntity<APIResponse<List<JournalEntry>>> getAll() {
        ServiceResult<List<JournalEntry>> result = journalService.getAllEntries();
        return toApiResponse(result, "Journal entries retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a journal entry by UUID")
    public ResponseEntity<APIResponse<JournalEntry>> getByUuid(@PathVariable String uuid) {
        ServiceResult<JournalEntry> result = journalService.getEntryByUuid(uuid);
        return toApiResponse(result, "Journal entry retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a journal entry")
    public ResponseEntity<APIResponse<JournalEntry>> update(
            @PathVariable String uuid, @Valid @RequestBody JournalEntry entry) {
        ServiceResult<JournalEntry> result = journalService.updateEntry(uuid, entry);
        return toApiResponse(result, "Journal entry updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a journal entry")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable String uuid) {
        ServiceResult<Void> result = journalService.deleteEntry(uuid);
        return toApiResponse(result, "Journal entry deleted successfully");
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get journal entries within a date range")
    public ResponseEntity<APIResponse<List<JournalEntry>>> getByDateRange(
            @RequestParam Long startDate,
            @RequestParam Long endDate) {
        ServiceResult<List<JournalEntry>> result = journalService.getEntriesByDateRange(startDate, endDate);
        return toApiResponse(result, "Journal entries retrieved successfully");
    }

    @GetMapping("/mood/{mood}")
    @Operation(summary = "Get journal entries by mood")
    public ResponseEntity<APIResponse<List<JournalEntry>>> getByMood(@PathVariable JournalMood mood) {
        ServiceResult<List<JournalEntry>> result = journalService.getEntriesByMood(mood);
        return toApiResponse(result, "Journal entries retrieved successfully");
    }

    @GetMapping("/stats")
    @Operation(summary = "Get journal statistics")
    public ResponseEntity<APIResponse<Map<String, Object>>> getStats() {
        ServiceResult<Map<String, Object>> result = journalService.getJournalStats();
        return toApiResponse(result, "Journal stats retrieved successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
