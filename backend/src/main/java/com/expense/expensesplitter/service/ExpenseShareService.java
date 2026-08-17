// File: C:\PROJECTS\expensesplitter\src\main\java\com\expense\expensesplitter\service\ExpenseShareService.java

package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.ExpenseShareDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.Expense;
import com.expense.expensesplitter.model.ExpenseShare;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.ExpenseRepository;
import com.expense.expensesplitter.repository.ExpenseShareRepository;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseShareService {

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ExpenseShareDTO createExpenseShare(ExpenseShare expenseShare) {
        if (expenseShare.getExpense() == null || expenseShare.getExpense().getId() == null) {
            throw new IllegalArgumentException("Expense is required for expense share");
        }
        if (expenseShare.getUser() == null || expenseShare.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required for expense share");
        }

        Expense expense = expenseRepository.findById(expenseShare.getExpense().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", expenseShare.getExpense().getId()));
        User user = userRepository.findById(expenseShare.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", expenseShare.getUser().getId()));

        expenseShare.setExpense(expense);
        expenseShare.setUser(user);

        ExpenseShare savedShare = expenseShareRepository.save(expenseShare);
        return mapToDTO(savedShare);
    }

    public ExpenseShareDTO getExpenseShareById(Long id) {
        ExpenseShare share = expenseShareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", id));
        return mapToDTO(share);
    }

    public List<ExpenseShareDTO> getAllExpenseShares() {
        return expenseShareRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExpenseShareDTO updateExpenseShare(Long id, ExpenseShare expenseShare) {
        ExpenseShare existingShare = expenseShareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", id));
        existingShare.setShareAmount(expenseShare.getShareAmount());
        existingShare.setSettled(expenseShare.isSettled());
        ExpenseShare updatedShare = expenseShareRepository.save(existingShare);
        return mapToDTO(updatedShare);
    }

    public void deleteExpenseShare(Long id) {
        ExpenseShare share = expenseShareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", id));
        expenseShareRepository.delete(share);
    }

    public List<ExpenseShareDTO> getSharesByExpense(Long expenseId) {
        return expenseShareRepository.findByExpenseId(expenseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseShareDTO> getSharesByUser(Long userId) {
        return expenseShareRepository.findByUser_Id(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseShareDTO> getSettledSharesByUser(Long userId) {
        return expenseShareRepository.findByUser_IdAndIsSettledTrue(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseShareDTO> getUnsettledSharesByUser(Long userId) {
        return expenseShareRepository.findByUser_IdAndIsSettledFalse(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ExpenseShareDTO mapToDTO(ExpenseShare share) {
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