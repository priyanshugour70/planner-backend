package com.planner.repositories.note;

import com.planner.entities.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserIdAndActiveTrueOrderByIsPinnedDescUpdatedAtDesc(Long userId);

    Optional<Note> findByUuidAndActiveTrue(String uuid);

    Optional<Note> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.isPinned = true AND n.active = true ORDER BY n.updatedAt DESC")
    List<Note> findPinnedNotes(@Param("userId") Long userId);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.category = :category AND n.active = true")
    List<Note> findByCategory(@Param("userId") Long userId, @Param("category") String category);

    long countByUserIdAndActiveTrue(Long userId);
}
