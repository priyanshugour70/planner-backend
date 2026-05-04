package com.planner.service.analytics.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.goal.Goal;
import com.planner.entities.habit.Habit;
import com.planner.entities.habit.HabitEntry;
import com.planner.entities.task.Task;
import com.planner.repositories.goal.GoalRepository;
import com.planner.repositories.goal.MilestoneRepository;
import com.planner.repositories.habit.HabitEntryRepository;
import com.planner.repositories.habit.HabitRepository;
import com.planner.repositories.journal.JournalEntryRepository;
import com.planner.repositories.note.NoteRepository;
import com.planner.repositories.task.TaskRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.analytics.AnalyticsService;
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
public class AnalyticsServiceImpl implements AnalyticsService {

    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final HabitEntryRepository habitEntryRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final NoteRepository noteRepository;

    @Override
    public ServiceResult<Map<String, Object>> getDashboardStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        log.debug("Getting dashboard stats for user: {}", userId);

        long totalGoals = goalRepository.countByUserIdAndActiveTrue(userId);
        long totalTasks = taskRepository.countByUserIdAndActiveTrue(userId);
        long completedTasks = taskRepository.countByUserIdAndIsCompletedTrueAndActiveTrue(userId);
        long totalHabits = habitRepository.countByUserIdAndIsActiveTrueAndActiveTrue(userId);
        long totalNotes = noteRepository.countByUserIdAndActiveTrue(userId);
        long totalJournals = journalEntryRepository.countByUserIdAndActiveTrue(userId);

        List<Goal> goals = goalRepository.findByUserIdAndActiveTrueOrderByNumberAsc(userId);
        float overallProgress = goals.isEmpty() ? 0f :
                (float) goals.stream().mapToDouble(Goal::getProgress).average().orElse(0);

        long todayStart = getTodayStartMillis();
        long todayEnd = todayStart + 86400000L;
        List<Task> todayTasks = taskRepository.findTasksByDateRange(userId, todayStart, todayEnd);
        long todayCompleted = todayTasks.stream().filter(Task::getIsCompleted).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalGoals", totalGoals);
        stats.put("totalTasks", totalTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("totalHabits", totalHabits);
        stats.put("totalNotes", totalNotes);
        stats.put("totalJournals", totalJournals);
        stats.put("overallProgress", overallProgress);
        stats.put("tasksCompletedToday", todayCompleted);
        stats.put("totalTasksToday", todayTasks.size());
        stats.put("completionRate", totalTasks > 0 ? (double) completedTasks / totalTasks : 0);

        return ServiceResult.ok(stats);
    }

    @Override
    public ServiceResult<Map<String, Object>> getGoalAnalytics() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        List<Goal> goals = goalRepository.findByUserIdAndActiveTrueOrderByNumberAsc(userId);

        List<Map<String, Object>> goalProgress = goals.stream().map(g -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("goalId", g.getUuid());
            item.put("goalTitle", g.getTitle());
            item.put("progress", g.getProgress());
            item.put("category", g.getCategory().name());
            item.put("milestonesCompleted", g.getMilestones().stream().filter(m -> Boolean.TRUE.equals(m.getIsCompleted())).count());
            item.put("totalMilestones", g.getMilestones().size());
            return item;
        }).toList();

        Map<String, Long> categoryBreakdown = new LinkedHashMap<>();
        goals.forEach(g -> categoryBreakdown.merge(g.getCategory().name(), 1L, Long::sum));

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalGoals", goals.size());
        analytics.put("averageProgress", goals.isEmpty() ? 0 : goals.stream().mapToDouble(Goal::getProgress).average().orElse(0));
        analytics.put("goalProgress", goalProgress);
        analytics.put("categoryBreakdown", categoryBreakdown);

        return ServiceResult.ok(analytics);
    }

    @Override
    public ServiceResult<Map<String, Object>> getTaskAnalytics() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        List<Task> allTasks = taskRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
        long total = allTasks.size();
        long completed = allTasks.stream().filter(Task::getIsCompleted).count();
        long overdue = allTasks.stream()
                .filter(t -> !t.getIsCompleted() && t.getDueDate() != null && t.getDueDate() < System.currentTimeMillis())
                .count();

        Map<String, Long> byPriority = new LinkedHashMap<>();
        allTasks.forEach(t -> byPriority.merge(t.getPriority().name(), 1L, Long::sum));

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalTasks", total);
        analytics.put("completedTasks", completed);
        analytics.put("overdueTasks", overdue);
        analytics.put("completionRate", total > 0 ? (double) completed / total : 0);
        analytics.put("byPriority", byPriority);

        return ServiceResult.ok(analytics);
    }

    @Override
    public ServiceResult<Map<String, Object>> getHabitAnalytics() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User not authenticated")));
        }

        List<Habit> habits = habitRepository.findActiveHabits(userId);
        List<HabitEntry> entries = habitEntryRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId);

        List<Map<String, Object>> habitStreaks = habits.stream().map(h -> {
            List<HabitEntry> habitEntries = entries.stream()
                    .filter(e -> e.getHabitId().equals(h.getUuid()))
                    .toList();

            long completedCount = habitEntries.stream().filter(HabitEntry::getIsCompleted).count();
            int currentStreak = calculateCurrentStreak(habitEntries);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("habitId", h.getUuid());
            item.put("habitTitle", h.getTitle());
            item.put("currentStreak", currentStreak);
            item.put("totalCompletions", completedCount);
            item.put("completionRate", habitEntries.isEmpty() ? 0 : (double) completedCount / habitEntries.size());
            return item;
        }).toList();

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalHabits", habits.size());
        analytics.put("habitStreaks", habitStreaks);

        return ServiceResult.ok(analytics);
    }

    private int calculateCurrentStreak(List<HabitEntry> entries) {
        if (entries.isEmpty()) return 0;
        int streak = 0;
        long oneDayMs = 86400000L;
        long today = getTodayStartMillis();

        for (int i = 0; i < entries.size(); i++) {
            HabitEntry entry = entries.get(i);
            long entryDay = entry.getDate() / oneDayMs * oneDayMs;
            long expectedDay = today - ((long) i * oneDayMs);

            if (entryDay == expectedDay && Boolean.TRUE.equals(entry.getIsCompleted())) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private long getTodayStartMillis() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
