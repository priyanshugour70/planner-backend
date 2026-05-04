package com.planner.repositories.note;

import com.planner.entities.note.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByUserIdAndActiveTrueOrderByIsPinnedDescUpdatedAtDesc(Long userId, Pageable pageable);

    List<Note> findByUserIdAndActiveTrueOrderByIsPinnedDescUpdatedAtDesc(Long userId);

    Optional<Note> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.active = true AND n.isPinned = true ORDER BY n.updatedAt DESC")
    List<Note> findPinnedNotes(@Param("userId") Long userId);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.active = true AND n.category = :category ORDER BY n.updatedAt DESC")
    Page<Note> findByCategory(@Param("userId") Long userId, @Param("category") String category, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.active = true AND n.category = :category ORDER BY n.updatedAt DESC")
    List<Note> findByCategory(@Param("userId") Long userId, @Param("category") String category);

    long countByUserIdAndActiveTrue(Long userId);
}
