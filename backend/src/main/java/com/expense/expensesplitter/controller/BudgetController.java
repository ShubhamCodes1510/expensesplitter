package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.BudgetDTO;
import com.expense.expensesplitter.model.Budget;
import com.expense.expensesplitter.service.BudgetService;
import com.expense.expensesplitter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@Valid @RequestBody Budget budget,
                                                   @RequestParam(required = false) Long userId) {
        if (budget.getUser() == null || budget.getUser().getId() == null) {
            budget.setUser(userService.getCurrentUser(userId));
        }
        BudgetDTO createdBudget = budgetService.createBudget(budget);
        return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BudgetDTO>> getUserBudgets(
            @RequestParam(required = false) Long userId) {
        userId = userService.getCurrentUser(userId).getId();
        List<BudgetDTO> budgets = budgetService.getUserBudgets(userId);
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BudgetDTO>> getActiveUserBudgets(
            @RequestParam(required = false) Long userId) {
        userId = userService.getCurrentUser(userId).getId();
        List<BudgetDTO> budgets = budgetService.getActiveUserBudgets(userId);
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id) {
        BudgetDTO budget = budgetService.getBudgetById(id);
        return new ResponseEntity<>(budget, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable Long id, @Valid @RequestBody Budget budgetUpdates) {
        BudgetDTO updatedBudget = budgetService.updateBudget(id, budgetUpdates);
        return new ResponseEntity<>(updatedBudget, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/summary/{period}")
    public ResponseEntity<BudgetDTO> getBudgetSummary(@PathVariable String period,
                                                       @RequestParam(required = false) Long userId) {
        userId = userService.getCurrentUser(userId).getId();
        BudgetDTO summary = budgetService.getBudgetSummary(userId, period);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}