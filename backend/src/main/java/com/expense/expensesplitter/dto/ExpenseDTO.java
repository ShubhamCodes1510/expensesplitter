package com.expense.expensesplitter.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {
    private Long id;
    private Long paidById;
    private String paidByName;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String comments;
    private BigDecimal amount;
    private LocalDateTime date;
    private List<ExpenseShareDTO> shares;
    private List<ReceiptDTO> receipts;
}