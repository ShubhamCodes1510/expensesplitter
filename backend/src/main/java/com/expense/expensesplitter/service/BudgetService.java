package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.BudgetDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.Budget;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.model.Category;
import com.expense.expensesplitter.model.Expense;
import com.expense.expensesplitter.repository.BudgetRepository;
import com.expense.expensesplitter.repository.UserRepository;
import com.expense.expensesplitter.repository.CategoryRepository;
import com.expense.expensesplitter.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public BudgetDTO createBudget(Budget budget) {
        // Validate user
        User user = userRepository.findById(budget.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", budget.getUser().getId()));
        budget.setUser(user);

        // Validate category if provided
        if (budget.getCategory() != null && budget.getCategory().getId() != null) {
            Category category = categoryRepository.findById(budget.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", budget.getCategory().getId()));
            budget.setCategory(category);
        } else {
            budget.setCategory(null); // Overall budget
        }

        // Set default values
        if (budget.getIsActive() == null) {
            budget.setIsActive(true);
        }
        if (budget.getAlertThreshold() == null) {
            budget.setAlertThreshold(BigDecimal.valueOf(80));
        }

        Budget savedBudget = budgetRepository.save(budget);
        return mapToDTO(savedBudget);
    }

    public List<BudgetDTO> getUserBudgets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        List<Budget> budgets = budgetRepository.findByUser(user);
        return budgets.stream()
                .map(this::mapToDTOWithSpending)
                .collect(Collectors.toList());
    }

    public List<BudgetDTO> getActiveUserBudgets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        List<Budget> budgets = budgetRepository.findByUserAndIsActive(user, true);
        return budgets.stream()
                .map(this::mapToDTOWithSpending)
                .collect(Collectors.toList());
    }

    public BudgetDTO getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));
        return mapToDTOWithSpending(budget);
    }

    @Transactional
    public BudgetDTO updateBudget(Long id, Budget budgetUpdates) {
        Budget existingBudget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));

        // Update fields if provided
        if (budgetUpdates.getAmount() != null) {
            existingBudget.setAmount(budgetUpdates.getAmount());
        }
        if (budgetUpdates.getPeriod() != null) {
            existingBudget.setPeriod(budgetUpdates.getPeriod());
        }
        if (budgetUpdates.getStartDate() != null) {
            existingBudget.setStartDate(budgetUpdates.getStartDate());
        }
        if (budgetUpdates.getEndDate() != null) {
            existingBudget.setEndDate(budgetUpdates.getEndDate());
        }
        if (budgetUpdates.getIsActive() != null) {
            existingBudget.setIsActive(budgetUpdates.getIsActive());
        }
        if (budgetUpdates.getDescription() != null) {
            existingBudget.setDescription(budgetUpdates.getDescription());
        }
        if (budgetUpdates.getAlertThreshold() != null) {
            existingBudget.setAlertThreshold(budgetUpdates.getAlertThreshold());
        }

        // Update category if provided
        if (budgetUpdates.getCategory() != null && budgetUpdates.getCategory().getId() != null) {
            Category category = categoryRepository.findById(budgetUpdates.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", budgetUpdates.getCategory().getId()));
            existingBudget.setCategory(category);
        } else if (budgetUpdates.getCategory() != null && budgetUpdates.getCategory().getId() == null) {
            existingBudget.setCategory(null); // Set to overall budget
        }

        Budget updatedBudget = budgetRepository.save(existingBudget);
        return mapToDTOWithSpending(updatedBudget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));
        budgetRepository.delete(budget);
    }

    public BudgetDTO getBudgetSummary(Long userId, String period) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        List<Budget> activeBudgets = budgetRepository.findActiveBudgetsForUserAndPeriod(user, period, LocalDateTime.now());
        
        // Create a summary DTO
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;
        
        for (Budget budget : activeBudgets) {
            totalBudget = totalBudget.add(budget.getAmount());
            BigDecimal spent = calculateSpentAmount(budget);
            totalSpent = totalSpent.add(spent);
        }
        
        BigDecimal remaining = totalBudget.subtract(totalSpent);
        BigDecimal percentageUsed = totalBudget.compareTo(BigDecimal.ZERO) > 0 
                ? totalSpent.divide(totalBudget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        return BudgetDTO.builder()
                .amount(totalBudget)
                .spentAmount(totalSpent)
                .remainingAmount(remaining)
                .percentageUsed(percentageUsed)
                .build();
    }

    private BudgetDTO mapToDTO(Budget budget) {
        return BudgetDTO.builder()
                .id(budget.getId())
                .userId(budget.getUser().getId())
                .userName(budget.getUser().getName())
                .categoryId(budget.getCategory() != null ? budget.getCategory().getId() : null)
                .categoryName(budget.getCategory() != null ? budget.getCategory().getName() : null)
                .amount(budget.getAmount())
                .period(budget.getPeriod())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .isActive(budget.getIsActive())
                .description(budget.getDescription())
                .alertThreshold(budget.getAlertThreshold())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }

    private BudgetDTO mapToDTOWithSpending(Budget budget) {
        BudgetDTO dto = mapToDTO(budget);
        BigDecimal spentAmount = calculateSpentAmount(budget);
        BigDecimal remainingAmount = budget.getAmount().subtract(spentAmount);
        BigDecimal percentageUsed = budget.getAmount().compareTo(BigDecimal.ZERO) > 0 
                ? spentAmount.divide(budget.getAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        dto.setSpentAmount(spentAmount);
        dto.setRemainingAmount(remainingAmount);
        dto.setPercentageUsed(percentageUsed);
        
        return dto;
    }

    private BigDecimal calculateSpentAmount(Budget budget) {
        LocalDateTime startDate = budget.getStartDate();
        LocalDateTime endDate = budget.getEndDate();
        LocalDateTime now = LocalDateTime.now();
        
        // Determine date range based on period
        if (startDate == null || endDate == null) {
            // If no date range specified, use current period based on period type
            if ("MONTHLY".equals(budget.getPeriod())) {
                startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
            } else if ("WEEKLY".equals(budget.getPeriod())) {
                startDate = now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);
                endDate = startDate.plusDays(6).withHour(23).withMinute(59).withSecond(59);
            } else if ("YEARLY".equals(budget.getPeriod())) {
                startDate = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
                endDate = now.withDayOfYear(now.toLocalDate().lengthOfYear()).withHour(23).withMinute(59).withSecond(59);
            } else {
                // Default to all time if no period logic
                startDate = LocalDateTime.MIN;
                endDate = LocalDateTime.MAX;
            }
        }
        
        // Query expenses for the user within date range
        List<Expense> expenses;
        if (budget.getCategory() != null) {
            // Category-specific budget
            expenses = expenseRepository.findByPaidByAndCategoryAndDateBetween(
                    budget.getUser(), budget.getCategory(), startDate, endDate);
        } else {
            // Overall budget
            expenses = expenseRepository.findByPaidByAndDateBetween(
                    budget.getUser(), startDate, endDate);
        }
        
        // Sum up expenses
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}