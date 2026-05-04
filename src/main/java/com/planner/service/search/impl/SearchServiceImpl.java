package com.planner.service.search.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.finance.Transaction;
import com.planner.entities.goal.Goal;
import com.planner.entities.habit.Habit;
import com.planner.entities.journal.JournalEntry;
import com.planner.entities.note.Note;
import com.planner.entities.reminder.Reminder;
import com.planner.entities.task.Task;
import com.planner.repositories.finance.TransactionRepository;
import com.planner.repositories.goal.GoalRepository;
import com.planner.repositories.habit.HabitRepository;
import com.planner.repositories.journal.JournalEntryRepository;
import com.planner.repositories.note.NoteRepository;
import com.planner.repositories.reminder.ReminderRepository;
import com.planner.repositories.task.TaskRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final ReminderRepository reminderRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public ServiceResult<List<Map<String, Object>>> search(String query, List<String> types) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        if (query == null || query.trim().isEmpty()) {
            return ServiceResult.ok(List.of());
        }

        log.debug("Searching for '{}' in types: {} for user: {}", query, types, userId);
        String lowerQuery = query.toLowerCase();
        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> searchTypes = types != null && !types.isEmpty() ? new HashSet<>(types) : null;

        if (shouldSearch(searchTypes, "GOAL")) {
            List<Goal> goals = goalRepository.findByUserIdAndActiveTrueOrderByNumberAsc(userId);
            goals.stream()
                    .filter(g -> matches(g.getTitle(), lowerQuery) || matches(g.getDescription(), lowerQuery))
                    .forEach(g -> results.add(createResult(g.getUuid(), g.getTitle(), g.getDescription(), "GOAL", null)));
        }

        if (shouldSearch(searchTypes, "TASK")) {
            List<Task> tasks = taskRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
            tasks.stream()
                    .filter(t -> matches(t.getTitle(), lowerQuery) || matches(t.getDescription(), lowerQuery))
                    .forEach(t -> results.add(createResult(t.getUuid(), t.getTitle(), t.getDescription(), "TASK", t.getDueDate())));
        }

        if (shouldSearch(searchTypes, "NOTE")) {
            List<Note> notes = noteRepository.findByUserIdAndActiveTrueOrderByIsPinnedDescUpdatedAtDesc(userId);
            notes.stream()
                    .filter(n -> matches(n.getTitle(), lowerQuery) || matches(n.getContent(), lowerQuery))
                    .forEach(n -> results.add(createResult(n.getUuid(), n.getTitle(), truncate(n.getContent(), 100), "NOTE", null)));
        }

        if (shouldSearch(searchTypes, "HABIT")) {
            List<Habit> habits = habitRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
            habits.stream()
                    .filter(h -> matches(h.getTitle(), lowerQuery) || matches(h.getDescription(), lowerQuery))
                    .forEach(h -> results.add(createResult(h.getUuid(), h.getTitle(), h.getDescription(), "HABIT", null)));
        }

        if (shouldSearch(searchTypes, "JOURNAL")) {
            List<JournalEntry> entries = journalEntryRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);
            entries.stream()
                    .filter(j -> matches(j.getTitle(), lowerQuery) || matches(j.getContent(), lowerQuery))
                    .forEach(j -> results.add(createResult(j.getUuid(), j.getTitle(), truncate(j.getContent(), 100), "JOURNAL", j.getDate())));
        }

        if (shouldSearch(searchTypes, "REMINDER")) {
            List<Reminder> reminders = reminderRepository.findByUserIdAndActiveTrueOrderByReminderTimeAsc(userId);
            reminders.stream()
                    .filter(r -> matches(r.getTitle(), lowerQuery) || matches(r.getDescription(), lowerQuery))
                    .forEach(r -> results.add(createResult(r.getUuid(), r.getTitle(), r.getDescription(), "REMINDER", r.getReminderTime())));
        }

        if (shouldSearch(searchTypes, "FINANCE")) {
            List<Transaction> transactions = transactionRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);
            transactions.stream()
                    .filter(t -> matches(t.getNote(), lowerQuery) || matches(t.getPersonName(), lowerQuery))
                    .forEach(t -> results.add(createResult(t.getUuid(), t.getCategory().name() + " - " + t.getAmount(), t.getNote(), "FINANCE", t.getDate())));
        }

        log.debug("Search returned {} results for query: '{}'", results.size(), query);
        return ServiceResult.ok(results);
    }

    private boolean shouldSearch(Set<String> types, String type) {
        return types == null || types.contains(type);
    }

    private boolean matches(String text, String query) {
        return text != null && text.toLowerCase().contains(query);
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }

    private Map<String, Object> createResult(String id, String title, String description, String type, Long date) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("description", description != null ? description : "");
        result.put("type", type);
        if (date != null) result.put("date", date);
        return result;
    }
}
