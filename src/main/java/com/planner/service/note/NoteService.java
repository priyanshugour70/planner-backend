package com.planner.service.note;

import com.planner.dtos.ServiceResult;
import com.planner.entities.note.Note;

import java.util.List;

public interface NoteService {

    ServiceResult<Note> createNote(Note note);

    ServiceResult<Note> updateNote(String uuid, Note note);

    ServiceResult<Void> deleteNote(String uuid);

    ServiceResult<Note> getNoteByUuid(String uuid);

    ServiceResult<List<Note>> getAllNotes();

    ServiceResult<List<Note>> getPinnedNotes();

    ServiceResult<List<Note>> getNotesByCategory(String category);
}
