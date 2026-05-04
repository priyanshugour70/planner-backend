package com.planner.service.finance;

import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.finance.Budget;
import com.planner.entities.finance.Transaction;
import com.planner.enums.TransactionType;

import java.util.List;
import java.util.Map;

public interface FinanceService {

    ServiceResult<Transaction> createTransaction(Transaction transaction);

    ServiceResult<Transaction> updateTransaction(String uuid, Transaction transaction);

    ServiceResult<Void> deleteTransaction(String uuid);

    ServiceResult<Pagination<Transaction>> getAllTransactions(int page, int size);

    ServiceResult<Pagination<Transaction>> getTransactionsByType(TransactionType type, int page, int size);

    ServiceResult<Pagination<Transaction>> getTransactionsByDateRange(Long startDate, Long endDate, int page, int size);

    ServiceResult<Budget> createBudget(Budget budget);

    ServiceResult<Budget> updateBudget(String uuid, Budget budget);

    ServiceResult<Void> deleteBudget(String uuid);

    ServiceResult<List<Budget>> getAllBudgets();

    ServiceResult<Map<String, Object>> getFinanceStats();

    ServiceResult<Transaction> settleDebt(String uuid);

    ServiceResult<List<Transaction>> getUnsettledDebts();
}
