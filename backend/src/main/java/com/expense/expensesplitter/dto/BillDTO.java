package com.expense.expensesplitter.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal amount;
    private String frequency;
    private LocalDateTime nextDueDate;
    private boolean isActive;
}