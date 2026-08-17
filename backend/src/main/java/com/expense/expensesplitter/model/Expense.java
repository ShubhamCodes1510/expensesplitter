package com.expense.expensesplitter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_id", nullable = false, referencedColumnName = "id")
    private User paidBy;

    @Transient
    private Long paidById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;

    @Column(length = 1000)
    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull(message = "Date is required")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SplitType splitType = SplitType.EQUAL;

    public enum SplitType {
        EQUAL,
        CUSTOM,
        PERCENTAGE,
        SHARES
    }

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpenseShare> shares = new ArrayList<>();

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Receipt> receipts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}