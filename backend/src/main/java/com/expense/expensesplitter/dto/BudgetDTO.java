package com.expense.expensesplitter.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private String description;
    private BigDecimal alertThreshold;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}