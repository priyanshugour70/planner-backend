package com.planner.repositories.goal;

import com.planner.entities.goal.Goal;
import com.planner.enums.GoalCategory;
import com.planner.enums.GoalPriority;
import com.planner.enums.GoalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Goal> {

    Page<Goal> findByUserIdAndActiveTrueOrderByIsPinnedDescNumberAsc(Long userId, Pageable pageable);

    List<Goal> findByUserIdAndActiveTrueOrderByIsPinnedDescNumberAsc(Long userId);

    Page<Goal> findByUserIdAndActiveTrueOrderByNumberAsc(Long userId, Pageable pageable);

    List<Goal> findByUserIdAndActiveTrueOrderByNumberAsc(Long userId);

    Optional<Goal> findByUuidAndActiveTrue(String uuid);

    Optional<Goal> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    List<Goal> findByUserIdAndCategoryAndActiveTrue(Long userId, GoalCategory category);

    List<Goal> findByUserIdAndStatusAndActiveTrue(Long userId, GoalStatus status);

    List<Goal> findByUserIdAndPriorityAndActiveTrue(Long userId, GoalPriority priority);

    List<Goal> findByUserIdAndIsFavoriteTrueAndActiveTrue(Long userId);

    List<Goal> findByUserIdAndIsPinnedTrueAndActiveTrue(Long userId);

    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.active = true AND g.progress < 1.0 ORDER BY g.progress DESC")
    List<Goal> findInProgressGoals(@Param("userId") Long userId);

    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.active = true " +
            "AND g.targetDate IS NOT NULL AND g.targetDate < :now AND g.status != 'COMPLETED' " +
            "ORDER BY g.targetDate ASC")
    List<Goal> findOverdueGoals(@Param("userId") Long userId, @Param("now") Long now);

    @Query("SELECT g FROM Goal g WHERE g.userId = :userId AND g.active = true " +
            "AND (LOWER(g.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(g.tags) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Goal> searchGoals(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    long countByUserIdAndActiveTrue(Long userId);

    long countByUserIdAndActiveTrueAndStatus(Long userId, GoalStatus status);

    long countByUserIdAndActiveTrueAndIsFavoriteTrue(Long userId);

    @Query("SELECT AVG(g.progress) FROM Goal g WHERE g.userId = :userId AND g.active = true")
    Double averageProgressByUser(@Param("userId") Long userId);

    @Query("SELECT g.category, COUNT(g) FROM Goal g WHERE g.userId = :userId AND g.active = true GROUP BY g.category")
    List<Object[]> countByCategory(@Param("userId") Long userId);

    @Query("SELECT g.priority, COUNT(g) FROM Goal g WHERE g.userId = :userId AND g.active = true GROUP BY g.priority")
    List<Object[]> countByPriority(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Goal g SET g.number = :number WHERE g.uuid = :uuid AND g.userId = :userId")
    void updateGoalNumber(@Param("uuid") String uuid, @Param("userId") Long userId, @Param("number") Integer number);
}
