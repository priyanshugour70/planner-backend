package com.planner.repositories.finance;

import com.planner.entities.finance.Budget;
import com.planner.enums.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);

    Optional<Budget> findByUuidAndActiveTrue(String uuid);

    Optional<Budget> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    Optional<Budget> findByUserIdAndCategoryAndActiveTrue(Long userId, TransactionCategory category);
}
