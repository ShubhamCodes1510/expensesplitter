package com.expense.expensesplitter.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private Long referenceId;
    private String referenceType;
    private LocalDateTime createdAt;
}
