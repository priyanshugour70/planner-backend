package com.planner.service.finance.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.Pagination;
import com.planner.dtos.ServiceResult;
import com.planner.entities.finance.Budget;
import com.planner.entities.finance.FinanceLog;
import com.planner.entities.finance.Transaction;
import com.planner.enums.TransactionType;
import com.planner.repositories.finance.BudgetRepository;
import com.planner.repositories.finance.FinanceLogRepository;
import com.planner.repositories.finance.TransactionRepository;
import com.planner.security.SecurityUtils;
import com.planner.service.finance.FinanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FinanceServiceImpl implements FinanceService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final FinanceLogRepository financeLogRepository;

    @Override
    public ServiceResult<Transaction> createTransaction(Transaction transaction) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating transaction for user: {}", userId);

        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setUserId(userId);
        transaction.setCreatedBy(userId);
        transaction.setActive(true);

        if (transaction.getIsSettled() == null) transaction.setIsSettled(false);
        if (transaction.getIsRecurring() == null) transaction.setIsRecurring(false);
        if (transaction.getDate() == null) transaction.setDate(System.currentTimeMillis());

        Transaction saved = transactionRepository.save(transaction);
        logFinanceAction(userId, "CREATE", "TRANSACTION",
                String.format("Created %s transaction of %.2f", saved.getType(), saved.getAmount()));
        log.info("Transaction created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Transaction> getTransactionByUuid(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching transaction uuid: {} for user: {}", uuid, userId);

        return transactionRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(ServiceResult::ok)
                .orElseGet(() -> ServiceResult.fail(HttpStatus.NOT_FOUND,
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Transaction not found"))));
    }

    @Override
    public ServiceResult<Transaction> updateTransaction(String uuid, Transaction transaction) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating transaction uuid: {} for user: {}", uuid, userId);

        return transactionRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (transaction.getAmount() != null) existing.setAmount(transaction.getAmount());
                    if (transaction.getType() != null) existing.setType(transaction.getType());
                    if (transaction.getCategory() != null) existing.setCategory(transaction.getCategory());
                    if (transaction.getNote() != null) existing.setNote(transaction.getNote());
                    if (transaction.getPersonName() != null) existing.setPersonName(transaction.getPersonName());
                    if (transaction.getDate() != null) existing.setDate(transaction.getDate());
                    if (transaction.getIsSettled() != null) existing.setIsSettled(transaction.getIsSettled());
                    if (transaction.getReceiptUri() != null) existing.setReceiptUri(transaction.getReceiptUri());
                    if (transaction.getIsRecurring() != null) existing.setIsRecurring(transaction.getIsRecurring());
                    if (transaction.getRecurringPeriod() != null) existing.setRecurringPeriod(transaction.getRecurringPeriod());
                    existing.setUpdatedBy(userId);

                    Transaction updated = transactionRepository.save(existing);
                    logFinanceAction(userId, "UPDATE", "TRANSACTION",
                            String.format("Updated transaction %s", uuid));
                    log.info("Transaction updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Transaction not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Transaction not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteTransaction(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting transaction uuid: {} for user: {}", uuid, userId);

        return transactionRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    transactionRepository.save(existing);
                    logFinanceAction(userId, "DELETE", "TRANSACTION",
                            String.format("Deleted transaction %s", uuid));
                    log.info("Transaction soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Transaction not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Transaction not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<Transaction>> getAllTransactions(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all transactions for user: {} page: {} size: {}", userId, page, size);

        Page<Transaction> txPage = transactionRepository.findByUserIdAndActiveTrueOrderByDateDesc(userId, PageRequest.of(page, size));
        Pagination<Transaction> pagination = Pagination.of(txPage.getContent(), page, size, txPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<Transaction>> getTransactionsByType(TransactionType type, int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching transactions by type: {} for user: {} page: {} size: {}", type, userId, page, size);

        Page<Transaction> txPage = transactionRepository.findByType(userId, type, PageRequest.of(page, size));
        Pagination<Transaction> pagination = Pagination.of(txPage.getContent(), page, size, txPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Pagination<Transaction>> getTransactionsByDateRange(Long startDate, Long endDate, int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching transactions by date range [{} - {}] for user: {} page: {} size: {}", startDate, endDate, userId, page, size);

        Page<Transaction> txPage = transactionRepository.findByDateRange(userId, startDate, endDate, PageRequest.of(page, size));
        Pagination<Transaction> pagination = Pagination.of(txPage.getContent(), page, size, txPage.getTotalElements());
        return ServiceResult.ok(pagination);
    }

    @Override
    public ServiceResult<Budget> createBudget(Budget budget) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating budget for user: {}", userId);

        budget.setUuid(UUID.randomUUID().toString());
        budget.setUserId(userId);
        budget.setCreatedBy(userId);
        budget.setActive(true);

        if (budget.getSpentAmount() == null) budget.setSpentAmount(0.0);

        Budget saved = budgetRepository.save(budget);
        logFinanceAction(userId, "CREATE", "BUDGET",
                String.format("Created budget for category %s with limit %.2f", saved.getCategory(), saved.getLimitAmount()));
        log.info("Budget created with uuid: {}", saved.getUuid());
        return ServiceResult.ok(saved);
    }

    @Override
    public ServiceResult<Budget> updateBudget(String uuid, Budget budget) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Updating budget uuid: {} for user: {}", uuid, userId);

        return budgetRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    if (budget.getCategory() != null) existing.setCategory(budget.getCategory());
                    if (budget.getLimitAmount() != null) existing.setLimitAmount(budget.getLimitAmount());
                    if (budget.getSpentAmount() != null) existing.setSpentAmount(budget.getSpentAmount());
                    if (budget.getPeriod() != null) existing.setPeriod(budget.getPeriod());
                    if (budget.getStartDate() != null) existing.setStartDate(budget.getStartDate());
                    if (budget.getEndDate() != null) existing.setEndDate(budget.getEndDate());
                    existing.setUpdatedBy(userId);

                    Budget updated = budgetRepository.save(existing);
                    logFinanceAction(userId, "UPDATE", "BUDGET",
                            String.format("Updated budget %s", uuid));
                    log.info("Budget updated: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Budget not found: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Budget not found")));
                });
    }

    @Override
    public ServiceResult<Void> deleteBudget(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Deleting budget uuid: {} for user: {}", uuid, userId);

        return budgetRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedBy(userId);
                    budgetRepository.save(existing);
                    logFinanceAction(userId, "DELETE", "BUDGET",
                            String.format("Deleted budget %s", uuid));
                    log.info("Budget soft-deleted: {}", uuid);
                    return ServiceResult.<Void>ok(null);
                })
                .orElseGet(() -> {
                    log.warn("Budget not found for deletion: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Budget not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Budget>> getAllBudgets() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching all budgets for user: {}", userId);

        List<Budget> budgets = budgetRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
        return ServiceResult.ok(budgets);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Map<String, Object>> getFinanceStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching finance stats for user: {}", userId);

        Double totalIncome = transactionRepository.sumByType(userId, TransactionType.INCOME);
        Double totalExpense = transactionRepository.sumByType(userId, TransactionType.EXPENSE);
        Double totalBorrowed = transactionRepository.sumByType(userId, TransactionType.BORROWED);
        Double totalLent = transactionRepository.sumByType(userId, TransactionType.LENT);

        List<Transaction> unsettled = transactionRepository.findUnsettledDebts(userId);
        List<Budget> budgets = budgetRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId);
        List<FinanceLog> recentLogs = financeLogRepository.findTop50ByUserIdAndActiveTrueOrderByTimestampMillisDesc(userId);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalIncome", totalIncome);
        stats.put("totalExpense", totalExpense);
        stats.put("totalBorrowed", totalBorrowed);
        stats.put("totalLent", totalLent);
        stats.put("balance", totalIncome - totalExpense);
        stats.put("unsettledDebtsCount", unsettled.size());
        stats.put("activeBudgets", budgets.size());
        stats.put("recentActivity", recentLogs);

        return ServiceResult.ok(stats);
    }

    @Override
    public ServiceResult<Transaction> settleDebt(String uuid) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Settling debt uuid: {} for user: {}", uuid, userId);

        return transactionRepository.findByUuidAndUserIdAndActiveTrue(uuid, userId)
                .map(existing -> {
                    TransactionType type = existing.getType();
                    if (type != TransactionType.BORROWED && type != TransactionType.LENT) {
                        return ServiceResult.<Transaction>fail(HttpStatus.BAD_REQUEST,
                                List.of(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Only borrowed/lent transactions can be settled")));
                    }

                    existing.setIsSettled(true);
                    existing.setUpdatedBy(userId);
                    Transaction updated = transactionRepository.save(existing);
                    logFinanceAction(userId, "SETTLE", "TRANSACTION",
                            String.format("Settled %s of %.2f with %s", type, existing.getAmount(), existing.getPersonName()));
                    log.info("Debt settled: {}", uuid);
                    return ServiceResult.ok(updated);
                })
                .orElseGet(() -> {
                    log.warn("Transaction not found for settlement: {}", uuid);
                    return ServiceResult.fail(HttpStatus.NOT_FOUND,
                            List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Transaction not found")));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<Transaction>> getUnsettledDebts() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching unsettled debts for user: {}", userId);

        List<Transaction> debts = transactionRepository.findUnsettledDebts(userId);
        return ServiceResult.ok(debts);
    }

    private void logFinanceAction(Long userId, String action, String entityType, String description) {
        FinanceLog financeLog = FinanceLog.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .timestampMillis(System.currentTimeMillis())
                .description(description)
                .build();
        financeLog.setActive(true);
        financeLog.setCreatedBy(userId);
        financeLogRepository.save(financeLog);
    }
}
