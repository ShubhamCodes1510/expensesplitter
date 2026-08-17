package com.expense.expensesplitter.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryDTO {
    private Long userId;
    private String userName;
    private BigDecimal totalExpenses;
    private BigDecimal totalOwed;
    private BigDecimal totalSettled;
    private BigDecimal netBalance;
    private BigDecimal amountLent;
}