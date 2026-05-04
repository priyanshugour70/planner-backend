package com.planner.repositories.finance;

import com.planner.entities.finance.Transaction;
import com.planner.enums.TransactionCategory;
import com.planner.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdAndActiveTrueOrderByDateDesc(Long userId);

    Optional<Transaction> findByUuidAndActiveTrue(String uuid);

    Optional<Transaction> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.type = :type AND t.active = true ORDER BY t.date DESC")
    List<Transaction> findByType(@Param("userId") Long userId, @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.category = :category AND t.active = true ORDER BY t.date DESC")
    List<Transaction> findByCategory(@Param("userId") Long userId, @Param("category") TransactionCategory category);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.date BETWEEN :startDate AND :endDate AND t.active = true ORDER BY t.date DESC")
    List<Transaction> findByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userId = :userId AND t.type = :type AND t.active = true")
    Double sumByType(@Param("userId") Long userId, @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.type IN ('BORROWED', 'LENT') AND t.isSettled = false AND t.active = true")
    List<Transaction> findUnsettledDebts(@Param("userId") Long userId);
}
