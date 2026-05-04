package com.planner.repositories.finance;

import com.planner.entities.finance.Transaction;
import com.planner.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserIdAndActiveTrueOrderByDateDesc(Long userId, Pageable pageable);

    List<Transaction> findByUserIdAndActiveTrueOrderByDateDesc(Long userId);

    Optional<Transaction> findByUuidAndUserIdAndActiveTrue(String uuid, Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.active = true AND t.type = :type ORDER BY t.date DESC")
    Page<Transaction> findByType(@Param("userId") Long userId, @Param("type") TransactionType type, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.active = true AND t.type = :type ORDER BY t.date DESC")
    List<Transaction> findByType(@Param("userId") Long userId, @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.active = true AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    Page<Transaction> findByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.active = true AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findByDateRange(@Param("userId") Long userId, @Param("startDate") Long startDate, @Param("endDate") Long endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userId = :userId AND t.active = true AND t.type = :type")
    Double sumByType(@Param("userId") Long userId, @Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.active = true AND (t.type = 'BORROWED' OR t.type = 'LENT') AND t.isSettled = false ORDER BY t.date DESC")
    List<Transaction> findUnsettledDebts(@Param("userId") Long userId);

    long countByUserIdAndActiveTrue(Long userId);
}
