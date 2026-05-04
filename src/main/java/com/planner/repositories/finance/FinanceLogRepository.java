package com.planner.repositories.finance;

import com.planner.entities.finance.FinanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceLogRepository extends JpaRepository<FinanceLog, Long> {

    List<FinanceLog> findByUserIdAndActiveTrueOrderByTimestampMillisDesc(Long userId);

    List<FinanceLog> findTop50ByUserIdAndActiveTrueOrderByTimestampMillisDesc(Long userId);
}
