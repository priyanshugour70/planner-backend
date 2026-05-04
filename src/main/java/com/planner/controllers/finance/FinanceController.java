package com.planner.controllers.finance;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.entities.finance.Budget;
import com.planner.entities.finance.Transaction;
import com.planner.enums.TransactionType;
import com.planner.service.finance.FinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@Tag(name = "Finance", description = "Financial transaction and budget management")
public class FinanceController {

    private final FinanceService financeService;

    @PostMapping("/transactions")
    @Operation(summary = "Create a new transaction")
    public ResponseEntity<APIResponse<Transaction>> createTransaction(@Valid @RequestBody Transaction transaction) {
        ServiceResult<Transaction> result = financeService.createTransaction(transaction);
        return toApiResponse(result, "Transaction created successfully");
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions for the current user")
    public ResponseEntity<APIResponse<List<Transaction>>> getAllTransactions() {
        ServiceResult<List<Transaction>> result = financeService.getAllTransactions();
        return toApiResponse(result, "Transactions retrieved successfully");
    }

    @PutMapping("/transactions/{uuid}")
    @Operation(summary = "Update a transaction")
    public ResponseEntity<APIResponse<Transaction>> updateTransaction(
            @PathVariable String uuid, @Valid @RequestBody Transaction transaction) {
        ServiceResult<Transaction> result = financeService.updateTransaction(uuid, transaction);
        return toApiResponse(result, "Transaction updated successfully");
    }

    @DeleteMapping("/transactions/{uuid}")
    @Operation(summary = "Delete a transaction")
    public ResponseEntity<APIResponse<Void>> deleteTransaction(@PathVariable String uuid) {
        ServiceResult<Void> result = financeService.deleteTransaction(uuid);
        return toApiResponse(result, "Transaction deleted successfully");
    }

    @GetMapping("/transactions/type/{type}")
    @Operation(summary = "Get transactions by type")
    public ResponseEntity<APIResponse<List<Transaction>>> getTransactionsByType(@PathVariable TransactionType type) {
        ServiceResult<List<Transaction>> result = financeService.getTransactionsByType(type);
        return toApiResponse(result, "Transactions retrieved successfully");
    }

    @GetMapping("/transactions/date-range")
    @Operation(summary = "Get transactions within a date range")
    public ResponseEntity<APIResponse<List<Transaction>>> getTransactionsByDateRange(
            @RequestParam Long startDate, @RequestParam Long endDate) {
        ServiceResult<List<Transaction>> result = financeService.getTransactionsByDateRange(startDate, endDate);
        return toApiResponse(result, "Transactions retrieved successfully");
    }

    @PutMapping("/transactions/{uuid}/settle")
    @Operation(summary = "Settle a debt transaction")
    public ResponseEntity<APIResponse<Transaction>> settleDebt(@PathVariable String uuid) {
        ServiceResult<Transaction> result = financeService.settleDebt(uuid);
        return toApiResponse(result, "Debt settled successfully");
    }

    @GetMapping("/transactions/unsettled")
    @Operation(summary = "Get all unsettled debt transactions")
    public ResponseEntity<APIResponse<List<Transaction>>> getUnsettledDebts() {
        ServiceResult<List<Transaction>> result = financeService.getUnsettledDebts();
        return toApiResponse(result, "Unsettled debts retrieved successfully");
    }

    @PostMapping("/budgets")
    @Operation(summary = "Create a new budget")
    public ResponseEntity<APIResponse<Budget>> createBudget(@Valid @RequestBody Budget budget) {
        ServiceResult<Budget> result = financeService.createBudget(budget);
        return toApiResponse(result, "Budget created successfully");
    }

    @GetMapping("/budgets")
    @Operation(summary = "Get all budgets for the current user")
    public ResponseEntity<APIResponse<List<Budget>>> getAllBudgets() {
        ServiceResult<List<Budget>> result = financeService.getAllBudgets();
        return toApiResponse(result, "Budgets retrieved successfully");
    }

    @PutMapping("/budgets/{uuid}")
    @Operation(summary = "Update a budget")
    public ResponseEntity<APIResponse<Budget>> updateBudget(
            @PathVariable String uuid, @Valid @RequestBody Budget budget) {
        ServiceResult<Budget> result = financeService.updateBudget(uuid, budget);
        return toApiResponse(result, "Budget updated successfully");
    }

    @DeleteMapping("/budgets/{uuid}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<APIResponse<Void>> deleteBudget(@PathVariable String uuid) {
        ServiceResult<Void> result = financeService.deleteBudget(uuid);
        return toApiResponse(result, "Budget deleted successfully");
    }

    @GetMapping("/stats")
    @Operation(summary = "Get financial statistics")
    public ResponseEntity<APIResponse<Map<String, Object>>> getStats() {
        ServiceResult<Map<String, Object>> result = financeService.getFinanceStats();
        return toApiResponse(result, "Financial stats retrieved successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
