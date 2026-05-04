package com.planner.service.note;

import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.note.Note;

import java.util.List;

public interface NoteService {

    ServiceResult<Note> createNote(Note note);

    ServiceResult<Note> updateNote(String uuid, Note note);

    ServiceResult<Void> deleteNote(String uuid);

    ServiceResult<Note> getNoteByUuid(String uuid);

    ServiceResult<Pagination<Note>> getAllNotes(int page, int size);

    ServiceResult<List<Note>> getPinnedNotes();

    ServiceResult<Pagination<Note>> getNotesByCategory(String category, int page, int size);
}
