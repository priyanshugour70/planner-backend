package com.planner.entities.finance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planner.entities.BaseEntity;
import com.planner.enums.BudgetPeriod;
import com.planner.enums.TransactionCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Budget extends BaseEntity {

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private TransactionCategory category;

    @Column(name = "limit_amount", nullable = false)
    private Double limitAmount;

    @Column(name = "spent_amount")
    private Double spentAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "period")
    private BudgetPeriod period = BudgetPeriod.MONTHLY;

    @Column(name = "start_date")
    private Long startDate;

    @Column(name = "end_date")
    private Long endDate;

    @Column(name = "notified_at")
    private Long notifiedAt;
}
