package com.planner.repositories.goal;

import com.planner.entities.goal.Goal;
import com.planner.enums.GoalCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    Page<Goal> findByUserIdAndActiveTrueOrderByNumberAsc(Long userId, Pageable pageable);

    List<Goal> findByUserIdAndActiveTrueOrderByNumberAsc(Long userId);

    Optional<Goal> findByUuidAndActiveTrue(String uuid);

    Optional<Goal> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    List<Goal> findByUserIdAndCategoryAndActiveTrue(Long userId, GoalCategory category);

    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.active = true AND g.progress < 1.0 ORDER BY g.progress DESC")
    List<Goal> findInProgressGoals(@Param("userId") Long userId);

    long countByUserIdAndActiveTrue(Long userId);
}
