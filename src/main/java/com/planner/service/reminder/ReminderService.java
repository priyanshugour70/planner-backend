package com.planner.service.reminder;

import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.reminder.Reminder;

import java.util.List;

public interface ReminderService {

    ServiceResult<Reminder> createReminder(Reminder reminder);

    ServiceResult<Reminder> updateReminder(String uuid, Reminder reminder);

    ServiceResult<Void> deleteReminder(String uuid);

    ServiceResult<Reminder> getReminderByUuid(String uuid);

    ServiceResult<Pagination<Reminder>> getAllReminders(int page, int size);

    ServiceResult<List<Reminder>> getActiveReminders();

    ServiceResult<Reminder> completeReminder(String uuid);
}
