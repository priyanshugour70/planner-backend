package com.planner.service.habit;

import com.planner.dtos.ServiceResult;
import com.planner.entities.habit.Habit;
import com.planner.entities.habit.HabitEntry;

import java.util.List;
import java.util.Map;

public interface HabitService {

    ServiceResult<Habit> createHabit(Habit habit);

    ServiceResult<Habit> updateHabit(String uuid, Habit habit);

    ServiceResult<Void> deleteHabit(String uuid);

    ServiceResult<Habit> getHabitByUuid(String uuid);

    ServiceResult<List<Habit>> getAllHabits();

    ServiceResult<List<Habit>> getActiveHabits();

    ServiceResult<HabitEntry> logHabitEntry(HabitEntry entry);

    ServiceResult<List<HabitEntry>> getHabitEntries(String habitUuid);

    ServiceResult<Map<String, Object>> getHabitStats(String habitUuid);
}
