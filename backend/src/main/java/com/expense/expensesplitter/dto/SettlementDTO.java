package com.expense.expensesplitter.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementDTO {

    // For all endpoints
    private Long id;

    // For calculateSettlements() endpoint
    private Long fromUserId;
    private String fromUserName;
    private Long toUserId;
    private String toUserName;
    private BigDecimal amount;
    private Boolean isCompleted;

    // For getUserBalances() endpoint
    private Long userId;
    private String userName;
    private BigDecimal balanceAmount;

}