package com.expense.expensesplitter.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseShareDTO {
    private Long id;
    private Long expenseId;
    private Long userId;
    private String userName;
    private BigDecimal shareAmount;
    private boolean isSettled;
}