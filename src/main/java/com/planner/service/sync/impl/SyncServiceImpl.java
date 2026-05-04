package com.planner.service.sync.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.dtos.req.sync.SyncRequest;
import com.planner.dtos.res.sync.FullSyncResponse;
import com.planner.dtos.res.sync.SyncResponse;
import com.planner.entities.finance.Budget;
import com.planner.entities.finance.FinanceLog;
import com.planner.entities.finance.Transaction;
import com.planner.entities.goal.Goal;
import com.planner.entities.goal.Milestone;
import com.planner.entities.habit.Habit;
import com.planner.entities.habit.HabitEntry;
import com.planner.entities.journal.JournalEntry;
import com.planner.entities.note.Note;
import com.planner.entities.reminder.Reminder;
import com.planner.entities.task.CalendarEvent;
import com.planner.entities.task.Task;
import com.planner.repositories.finance.BudgetRepository;
import com.planner.repositories.finance.FinanceLogRepository;
import com.planner.repositories.finance.TransactionRepository;
import com.planner.repositories.goal.GoalRepository;
import com.planner.repositories.habit.HabitEntryRepository;
import com.planner.repositories.habit.HabitRepository;
import com.planner.repositories.journal.JournalEntryRepository;
import com.planner.repositories.note.NoteRepository;
import com.planner.repositories.reminder.ReminderRepository;
import com.planner.repositories.task.CalendarEventRepository;
import com.planner.repositories.task.TaskRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.sync.SyncService;
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
public class SyncServiceImpl implements SyncService {

    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;
    private final HabitEntryRepository habitEntryRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final FinanceLogRepository financeLogRepository;
    private final ReminderRepository reminderRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ServiceResult<SyncResponse> pushChanges(SyncRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        log.info("Processing sync push for user: {}, items: {}", userId,
                request.getItems() != null ? request.getItems().size() : 0);

        SyncResponse response = SyncResponse.builder()
                .serverTimestamp(System.currentTimeMillis())
                .version(3)
                .data(new HashMap<>())
                .conflicts(new ArrayList<>())
                .build();

        return ServiceResult.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<FullSyncResponse> pullAllData() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        log.info("Pulling all data for user: {}", userId);

        List<Goal> goals = goalRepository.findByUserIdAndActiveTrueOrderByNumberAsc(userId);
        List<Task> tasks = taskRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
        List<CalendarEvent> events = calendarEventRepository.findByUserIdAndActiveTrueOrderByDateAsc(userId);
        List<Note> notes = noteRepository.findByUserIdAndActiveTrueOrderByIsPinnedDescUpdatedAtDesc(userId);
        List<Habit> habits = habitRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
        List<HabitEntry> habitEntries = habitEntryRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);
        List<JournalEntry> journalEntries = journalEntryRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);
        List<Reminder> reminders = reminderRepository.findByUserIdAndActiveTrueOrderByReminderTimeAsc(userId);
        List<Transaction> transactions = transactionRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);
        List<Budget> budgets = budgetRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
        List<FinanceLog> financeLogs = financeLogRepository.findTop50ByUserIdAndActiveTrueOrderByTimestampMillisDesc(userId);

        FullSyncResponse response = FullSyncResponse.builder()
                .version(3)
                .exportedAt(System.currentTimeMillis())
                .goals(convertToMaps(goals))
                .tasks(convertToMaps(tasks))
                .events(convertToMaps(events))
                .notes(convertToMaps(notes))
                .habits(convertToMaps(habits))
                .habitEntries(convertToMaps(habitEntries))
                .journalEntries(convertToMaps(journalEntries))
                .reminders(convertToMaps(reminders))
                .transactions(convertToMaps(transactions))
                .budgets(convertToMaps(budgets))
                .financeLogs(convertToMaps(financeLogs))
                .build();

        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult<Void> fullSync(FullSyncResponse data) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        log.info("Full sync upload for user: {}", userId);
        return ServiceResult.ok(null);
    }

    @SuppressWarnings("unchecked")
    private <T> List<Map<String, Object>> convertToMaps(List<T> entities) {
        return entities.stream()
                .map(e -> objectMapper.convertValue(e, Map.class))
                .map(m -> (Map<String, Object>) m)
                .collect(Collectors.toList());
    }
}
