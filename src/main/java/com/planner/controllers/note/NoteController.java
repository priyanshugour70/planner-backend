package com.planner.controllers.note;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.note.Note;
import com.planner.service.note.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "Note management")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "Create a new note")
    public ResponseEntity<APIResponse<Note>> create(@Valid @RequestBody Note note) {
        ServiceResult<Note> result = noteService.createNote(note);
        return toApiResponse(result, "Note created successfully");
    }

    @GetMapping
    @Operation(summary = "Get all notes")
    public ResponseEntity<APIResponse<List<Note>>> getAll() {
        ServiceResult<List<Note>> result = noteService.getAllNotes();
        return toApiResponse(result, "Notes retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get a note by UUID")
    public ResponseEntity<APIResponse<Note>> getByUuid(@PathVariable String uuid) {
        ServiceResult<Note> result = noteService.getNoteByUuid(uuid);
        return toApiResponse(result, "Note retrieved successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a note")
    public ResponseEntity<APIResponse<Note>> update(@PathVariable String uuid, @Valid @RequestBody Note note) {
        ServiceResult<Note> result = noteService.updateNote(uuid, note);
        return toApiResponse(result, "Note updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a note")
    public ResponseEntity<APIResponse<Void>> delete(@PathVariable String uuid) {
        ServiceResult<Void> result = noteService.deleteNote(uuid);
        return toApiResponse(result, "Note deleted successfully");
    }

    @GetMapping("/pinned")
    @Operation(summary = "Get all pinned notes")
    public ResponseEntity<APIResponse<List<Note>>> getPinned() {
        ServiceResult<List<Note>> result = noteService.getPinnedNotes();
        return toApiResponse(result, "Pinned notes retrieved successfully");
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get notes by category")
    public ResponseEntity<APIResponse<List<Note>>> getByCategory(@PathVariable String category) {
        ServiceResult<List<Note>> result = noteService.getNotesByCategory(category);
        return toApiResponse(result, "Notes retrieved successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
