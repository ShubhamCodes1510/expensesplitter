package com.expense.expensesplitter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Share amount is required")
    @DecimalMin(value = "0.00", message = "Share amount cannot be negative")
    private BigDecimal shareAmount;

    @Column(name = "is_settled", nullable = false)
    @Builder.Default
    private boolean isSettled = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}