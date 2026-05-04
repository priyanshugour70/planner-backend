package com.planner.service.note.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.note.Note;
import com.planner.repositories.note.NoteRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.note.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    public ServiceResult<Note> createNote(Note note) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating note for user: {}", userId);

        note.setUuid(UUID.randomUUID().toString());
        note.setUserId(userId);
        note.setCreatedBy(userId);
        note.setActive(true);

        if (note.getIsPinned() == null) note.setIsPinned(false);
        if (note.getIsLocked() == null) note.setIsLocked(false);
        if (note.getCategory() == null) note.setCategory("General");
        if (note.getRecallCount() == null) note.setRecallCount(0);

        Note saved = noteRepository.save(note);
        log.info("Note created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    public ServiceResult<Note> updateNote(String uuid, Note note) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating note uuid: {} for user: {}", uuid, userId);

        return noteRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (note.getTitle() != null) existing.setTitle(note.getTitle());
                    if (note.getContent() != null) existing.setContent(note.getContent());
                    if (note.getColor() != null) existing.setColor(note.getColor());
                    if (note.getIsPinned() != null) existing.setIsPinned(note.getIsPinned());
                    if (note.getLinkedGoalId() != null) existing.setLinkedGoalId(note.getLinkedGoalId());
                    if (note.getLinkedTaskId() != null) existing.setLinkedTaskId(note.getLinkedTaskId());
                    if (note.getLinkedReminderId() != null) existing.setLinkedReminderId(note.getLinkedReminderId());
                    if (note.getTags() != null) existing.setTags(note.getTags());
                    if (note.getPriority() != null) existing.setPriority(note.getPriority());
                    if (note.getHasReminder() != null) existing.setHasReminder(note.getHasReminder());
                    if (note.getReminderTime() != null) existing.setReminderTime(note.getReminderTime());
                    if (note.getReminderRepeatType() != null) existing.setReminderRepeatType(note.getReminderRepeatType());
                    if (note.getIsReminderEnabled() != null) existing.setIsReminderEnabled(note.getIsReminderEnabled());
                    if (note.getNotificationId() != null) existing.setNotificationId(note.getNotificationId());
                    if (note.getIsLocked() != null) existing.setIsLocked(note.getIsLocked());
                    if (note.getCategory() != null) existing.setCategory(note.getCategory());
                    if (note.getMood() != null) existing.setMood(note.getMood());
                    if (note.getNextRecallDate() != null) existing.setNextRecallDate(note.getNextRecallDate());
                    if (note.getRecallCount() != null) existing.setRecallCount(note.getRecallCount());
                    existing.setUpdatedBy(userId);

                    Note updated = noteRepository.save(existing);
                    log.info("Note updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Note not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Note not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteNote(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting note uuid: {} for user: {}", uuid, userId);

        return noteRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    noteRepository.save(existing);
                    log.info("Note soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Note not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Note not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Note> getNoteByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching note uuid: {} for user: {}", uuid, userId);

        return noteRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(ServiceResult::ok)
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Note not found"))));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Note>> getAllNotes() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all notes for user: {}", userId);

        List<Note> notes = noteRepository.findByUserIdAndActiveTrueOrderByIsPinnedDescUpdatedAtDesc(userId);
        return ServiceResult.ok(notes);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Note>> getPinnedNotes() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching pinned notes for user: {}", userId);

        List<Note> notes = noteRepository.findPinnedNotes(userId);
        return ServiceResult.ok(notes);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Note>> getNotesByCategory(String category) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching notes by category: {} for user: {}", category, userId);

        List<Note> notes = noteRepository.findByCategory(userId, category);
        return ServiceResult.ok(notes);
    }
}
