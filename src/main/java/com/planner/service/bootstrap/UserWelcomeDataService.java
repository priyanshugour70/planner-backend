package com.planner.service.bootstrap;

import com.planner.entities.finance.Budget;
import com.planner.entities.finance.Transaction;
import com.planner.entities.goal.Goal;
import com.planner.entities.goal.Milestone;
import com.planner.entities.habit.Habit;
import com.planner.entities.habit.HabitEntry;
import com.planner.entities.journal.JournalEntry;
import com.planner.entities.note.Note;
import com.planner.entities.reminder.Reminder;
import com.planner.entities.settings.UserSettings;
import com.planner.entities.task.CalendarEvent;
import com.planner.entities.task.Subtask;
import com.planner.entities.task.Task;
import com.planner.enums.*;
import com.planner.repositories.finance.BudgetRepository;
import com.planner.repositories.finance.TransactionRepository;
import com.planner.repositories.goal.GoalRepository;
import com.planner.repositories.habit.HabitEntryRepository;
import com.planner.repositories.habit.HabitRepository;
import com.planner.repositories.journal.JournalEntryRepository;
import com.planner.repositories.note.NoteRepository;
import com.planner.repositories.reminder.ReminderRepository;
import com.planner.repositories.settings.UserSettingsRepository;
import com.planner.repositories.task.CalendarEventRepository;
import com.planner.repositories.task.TaskRepository;
import com.planner.repositories.auth.UserRepository;
import com.planner.entities.auth.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Seeds rich starter content once per user (guest, OTP signup, or password register).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserWelcomeDataService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;
    private final HabitEntryRepository habitEntryRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final ReminderRepository reminderRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Transactional
    public void seedForNewUser(Long userId) {
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty() || !Boolean.TRUE.equals(opt.get().getActive())) {
            return;
        }
        User user = opt.get();
        if (Boolean.TRUE.equals(user.getWelcomeDataSeeded())) {
            return;
        }

        long now = System.currentTimeMillis();
        long tomorrow = now + 86_400_000L;

        // --- Goals + milestones ---
        Goal gWelcome = buildGoal(userId, 1, "Get started with Planner", "Explore goals, tasks, and habits — everything here is sample data you can edit or delete.",
                GoalCategory.PERSONAL, "🎯", 0xFF6C63FFL, now + 30L * 86_400_000L);
        Milestone m1 = buildMilestone(userId, "Complete your profile", "Open Settings and set your name.");
        Milestone m2 = buildMilestone(userId, "Create your first real goal", "Tap Goals and add something you care about.");
        m1.setGoal(gWelcome);
        m2.setGoal(gWelcome);
        gWelcome.getMilestones().add(m1);
        gWelcome.getMilestones().add(m2);
        goalRepository.save(gWelcome);

        Goal gHealth = buildGoal(userId, 2, "Feel healthier this month", "Small daily wins add up.", GoalCategory.HEALTH, "💪", 0xFF4DD0E1L, now + 14L * 86_400_000L);
        Milestone hm1 = buildMilestone(userId, "Move every day", "Even a 10-minute walk counts.");
        hm1.setGoal(gHealth);
        gHealth.getMilestones().add(hm1);
        goalRepository.save(gHealth);

        // --- Tasks (link first goal) ---
        Task t1 = buildTask(userId, "Review today’s tasks", "Swipe or tap to complete items.", TaskPriority.HIGH, now + 86_400_000L, gWelcome.getUuid());
        Subtask st1 = buildSubtask(userId, t1, "Open the Tasks tab", false);
        t1.getSubtasks().add(st1);
        taskRepository.save(t1);

        Task t2 = buildTask(userId, "Plan your week", "Block time for your top 3 priorities.", TaskPriority.MEDIUM, tomorrow, null);
        taskRepository.save(t2);

        Task t3 = buildTask(userId, "Try the habit tracker", "Check off a habit for today.", TaskPriority.LOW, now, null);
        t3.setIsCompleted(true);
        t3.setCompletedAt(now);
        taskRepository.save(t3);

        // --- Notes ---
        noteRepository.save(buildNote(userId, "Quick tips", "• Break big goals into milestones\n• Set reminders for important work\n• Use finance to track spending", 0xFFFFF9C4L, true));
        noteRepository.save(buildNote(userId, "Ideas inbox", "Jot ideas here so nothing gets lost.", 0xFFE1BEE7L, false));

        // --- Habits ---
        Habit hWater = buildHabit(userId, "Drink water", "8 glasses", HabitType.BOOLEAN, List.of(1, 2, 3, 4, 5, 6, 7), HabitTimeOfDay.MORNING, 0xFF03A9F4L);
        habitRepository.save(hWater);
        Habit hWalk = buildHabit(userId, "Walk 20 minutes", "Fresh air and movement", HabitType.YES_NO, List.of(1, 2, 3, 4, 5), HabitTimeOfDay.EVENING, 0xFF8BC34AL);
        habitRepository.save(hWalk);
        Habit hRead = buildHabit(userId, "Read 10 pages", "Books or articles", HabitType.COUNTER, List.of(1, 2, 3, 4, 5, 6, 7), HabitTimeOfDay.ANY_TIME, 0xFFFF9800L);
        hRead.setTargetValue(10f);
        hRead.setUnit("pages");
        habitRepository.save(hRead);

        habitEntryRepository.save(buildHabitEntry(userId, hWater.getUuid(), now, true, 1f, "Great start!"));
        habitEntryRepository.save(buildHabitEntry(userId, hWalk.getUuid(), now, false, 0f, null));

        // --- Journal ---
        JournalEntry jr = JournalEntry.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .date(now)
                .title("First day in Planner")
                .content("I'm setting up my life dashboard — goals, habits, and calm routines.")
                .mood(JournalMood.HAPPY)
                .tags(new ArrayList<>(List.of("welcome", "gratitude")))
                .gratitude(new ArrayList<>(List.of("A fresh start", "Tools that help me focus")))
                .build();
        jr.setActive(true);
        jr.setCreatedBy(userId);
        journalEntryRepository.save(jr);

        // --- Reminders ---
        reminderRepository.save(buildReminder(userId, "Weekly review", "Reflect on wins and plan next week.", tomorrow + 10 * 3_600_000L, ItemPriority.P3));
        reminderRepository.save(buildReminder(userId, "Stretch break", "2 minutes away from the screen.", now + 4 * 3_600_000L, ItemPriority.P5));

        // --- Finance ---
        transactionRepository.save(buildTx(userId, 42.50, TransactionType.EXPENSE, TransactionCategory.FOOD, "Coffee & lunch", now - 86_400_000L));
        transactionRepository.save(buildTx(userId, 120.0, TransactionType.EXPENSE, TransactionCategory.TRANSPORT, "Transit pass", now - 2 * 86_400_000L));
        transactionRepository.save(buildTx(userId, 2500.0, TransactionType.INCOME, TransactionCategory.SALARY, "Sample income entry", now - 3 * 86_400_000L));

        Budget budget = Budget.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .category(TransactionCategory.FOOD)
                .limitAmount(400.0)
                .spentAmount(42.50)
                .period(BudgetPeriod.MONTHLY)
                .startDate(now - 7L * 86_400_000L)
                .build();
        budget.setActive(true);
        budget.setCreatedBy(userId);
        budgetRepository.save(budget);

        // --- Calendar ---
        CalendarEvent ev = CalendarEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .title("Focus block")
                .description("Deep work — phone on silent.")
                .date(now)
                .startTime(now + 9 * 3_600_000L)
                .endTime(now + 11 * 3_600_000L)
                .color(0xFF673AB7L)
                .priority(ItemPriority.P2)
                .linkedGoalId(gWelcome.getUuid())
                .build();
        ev.setActive(true);
        ev.setCreatedBy(userId);
        calendarEventRepository.save(ev);

        // --- Default settings row ---
        if (userSettingsRepository.findByUserIdAndActiveTrue(userId).isEmpty()) {
            UserSettings settings = UserSettings.builder()
                    .userId(userId)
                    .themeMode(ThemeMode.SYSTEM)
                    .notificationsEnabled(true)
                    .dailyReminderTime("08:00")
                    .weeklyReviewDay(0)
                    .build();
            settings.setActive(true);
            settings.setCreatedBy(userId);
            userSettingsRepository.save(settings);
        }

        user.setWelcomeDataSeeded(true);
        userRepository.save(user);
        log.info("Welcome sample data seeded for userId={}", userId);
    }

    private Goal buildGoal(Long userId, int number, String title, String desc, GoalCategory cat, String icon, long color, Long targetDate) {
        Goal g = Goal.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .number(number)
                .title(title)
                .description(desc)
                .category(cat)
                .icon(icon)
                .color(color)
                .progress(0f)
                .targetDate(targetDate)
                .milestones(new ArrayList<>())
                .build();
        g.setActive(true);
        g.setCreatedBy(userId);
        return g;
    }

    private Milestone buildMilestone(Long userId, String title, String description) {
        Milestone m = new Milestone();
        m.setUuid(UUID.randomUUID().toString());
        m.setTitle(title);
        m.setDescription(description);
        m.setIsCompleted(false);
        m.setActive(true);
        m.setCreatedBy(userId);
        return m;
    }

    private Task buildTask(Long userId, String title, String description, TaskPriority priority, Long dueDate, String linkedGoalId) {
        Task t = Task.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .title(title)
                .description(description)
                .isCompleted(false)
                .priority(priority)
                .dueDate(dueDate)
                .linkedGoalId(linkedGoalId)
                .subtasks(new ArrayList<>())
                .tags(new ArrayList<>(List.of("welcome")))
                .build();
        t.setActive(true);
        t.setCreatedBy(userId);
        return t;
    }

    private Subtask buildSubtask(Long userId, Task task, String title, boolean done) {
        Subtask s = Subtask.builder()
                .uuid(UUID.randomUUID().toString())
                .task(task)
                .title(title)
                .isCompleted(done)
                .build();
        s.setActive(true);
        s.setCreatedBy(userId);
        return s;
    }

    private Note buildNote(Long userId, String title, String content, long color, boolean pinned) {
        Note n = Note.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .title(title)
                .content(content)
                .color(color)
                .isPinned(pinned)
                .build();
        n.setActive(true);
        n.setCreatedBy(userId);
        return n;
    }

    private Habit buildHabit(Long userId, String title, String description, HabitType type,
                             List<Integer> frequency, HabitTimeOfDay timeOfDay, long iconColor) {
        Habit h = Habit.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .title(title)
                .description(description)
                .type(type)
                .frequency(new ArrayList<>(frequency))
                .timeOfDay(timeOfDay)
                .iconColor(iconColor)
                .targetValue(1f)
                .isActive(true)
                .build();
        h.setActive(true);
        h.setCreatedBy(userId);
        return h;
    }

    private HabitEntry buildHabitEntry(Long userId, String habitUuid, long date, boolean completed, float value, String notes) {
        HabitEntry e = HabitEntry.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .habitId(habitUuid)
                .date(date)
                .value(value)
                .isCompleted(completed)
                .notes(notes)
                .build();
        e.setActive(true);
        e.setCreatedBy(userId);
        return e;
    }

    private Reminder buildReminder(Long userId, String title, String description, long reminderTime, ItemPriority priority) {
        Reminder r = Reminder.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .title(title)
                .description(description)
                .reminderTime(reminderTime)
                .priority(priority)
                .repeatType(RepeatType.NONE)
                .isEnabled(true)
                .isCompleted(false)
                .build();
        r.setActive(true);
        r.setCreatedBy(userId);
        return r;
    }

    private Transaction buildTx(Long userId, double amount, TransactionType type, TransactionCategory category, String note, long date) {
        Transaction t = Transaction.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .amount(amount)
                .type(type)
                .category(category)
                .note(note)
                .date(date)
                .isSettled(false)
                .isRecurring(false)
                .build();
        t.setActive(true);
        t.setCreatedBy(userId);
        return t;
    }
}
