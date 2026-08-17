package com.expense.expensesplitter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotBlank(message = "Period is required")
    private String period; // MONTHLY, WEEKLY, YEARLY, CUSTOM

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(length = 500)
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Column(name = "alert_threshold")
    @DecimalMin(value = "0.0", message = "Alert threshold must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Alert threshold must be between 0 and 100")
    @Builder.Default
    private BigDecimal alertThreshold = BigDecimal.valueOf(80); // Percentage

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (alertThreshold == null) {
            alertThreshold = BigDecimal.valueOf(80);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}