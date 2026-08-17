package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.ExpenseDTO;
import com.expense.expensesplitter.dto.ExpenseShareDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.*;
import com.expense.expensesplitter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public ExpenseDTO addExpense(Expense expense) {
        // ✅ Step 1: Validate paidBy user (support both paidBy object and paidById)
        User paidBy;
        if (expense.getPaidBy() != null && expense.getPaidBy().getId() != null) {
            paidBy = userRepository.findById(expense.getPaidBy().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", expense.getPaidBy().getId()));
        } else if (expense.getPaidById() != null) {
            paidBy = userRepository.findById(expense.getPaidById())
                    .orElseThrow(() -> new ResourceNotFoundException("User", expense.getPaidById()));
        } else {
            throw new IllegalArgumentException("Either paidBy or paidById must be provided");
        }
        expense.setPaidBy(paidBy);

        // ✅ Step 2: Validate category if present
        if (expense.getCategory() != null && expense.getCategory().getId() != null) {
            Category category = categoryRepository.findById(expense.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", expense.getCategory().getId()));
            expense.setCategory(category);
        }

        // ✅ Step 3: Validate shares based on split type
        if (expense.getShares() == null || expense.getShares().isEmpty()) {
            throw new IllegalArgumentException("At least one user must be included in the expense");
        }

        // Validate and process shares
        List<ExpenseShare> shares = new ArrayList<>();
        BigDecimal totalShareAmount = BigDecimal.ZERO;
        
        for (ExpenseShare share : expense.getShares()) {
            Long userId = (share.getUser() != null && share.getUser().getId() != null)
                ? share.getUser().getId()
                : share.getUserId();
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required for each share");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));
            
            // Create new share with user reference
            ExpenseShare newShare = new ExpenseShare();
            newShare.setExpense(expense);
            newShare.setUser(user);
            
            // Set share amount based on split type
            if (expense.getSplitType() == Expense.SplitType.CUSTOM) {
                // For custom split, use the shareAmount from the incoming share
                if (share.getShareAmount() == null) {
                    throw new IllegalArgumentException("Share amount is required for custom split");
                }
                if (share.getShareAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Share amount cannot be negative");
                }
                newShare.setShareAmount(share.getShareAmount());
            } else {
                // For equal split, calculate equal share
                BigDecimal shareAmount = expense.getAmount()
                        .divide(BigDecimal.valueOf(expense.getShares().size()), 2, RoundingMode.HALF_UP);
                newShare.setShareAmount(shareAmount);
            }
            
            newShare.setSettled(false);
            shares.add(newShare);
            totalShareAmount = totalShareAmount.add(newShare.getShareAmount());
        }

        // ✅ Step 4: Validate total amount matches expense amount (with tolerance for rounding)
        if (expense.getSplitType() == Expense.SplitType.CUSTOM) {
            BigDecimal difference = totalShareAmount.subtract(expense.getAmount()).abs();
            if (difference.compareTo(new BigDecimal("0.01")) > 0) {
                throw new IllegalArgumentException(
                    String.format("Total share amount (%.2f) does not match expense amount (%.2f)",
                        totalShareAmount.doubleValue(), expense.getAmount().doubleValue())
                );
            }
        }

        // ✅ Step 5: Set shares to expense
        expense.setShares(shares);

        // ✅ Step 6: Expense Save Karein (Saath me shares bhi save honge)
        Expense savedExpense = expenseRepository.save(expense);

        return mapToDTO(savedExpense);
    }

    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        return mapToDTO(expense);
    }

    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseDTO> getExpensesByUser(User user) {
        return expenseRepository.findByPaidBy(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExpenseDTO updateExpense(Long id, Expense expense) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        existingExpense.setDescription(expense.getDescription());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setDate(expense.getDate());
        if (expense.getCategory() != null && expense.getCategory().getId() != null) {
            Category category = categoryRepository.findById(expense.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", expense.getCategory().getId()));
            existingExpense.setCategory(category);
        }
        Expense updatedExpense = expenseRepository.save(existingExpense);
        return mapToDTO(updatedExpense);
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        expenseRepository.delete(expense);
    }

    private ExpenseDTO mapToDTO(Expense expense) {
        List<ExpenseShareDTO> shareDTOs = expense.getShares().stream()
                .map(this::mapShareToDTO)
                .collect(Collectors.toList());

        return ExpenseDTO.builder()
                .id(expense.getId())
                .paidById(expense.getPaidBy().getId())
                .paidByName(expense.getPaidBy().getName())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : null)
                .description(expense.getDescription())
                .comments(expense.getComments())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .shares(shareDTOs)
                .build();
    }

    private ExpenseShareDTO mapShareToDTO(ExpenseShare share) {
        return ExpenseShareDTO.builder()
                .id(share.getId())
                .expenseId(share.getExpense().getId())
                .userId(share.getUser().getId())
                .userName(share.getUser().getName())
                .shareAmount(share.getShareAmount())
                .isSettled(share.isSettled())
                .build();
    }
}