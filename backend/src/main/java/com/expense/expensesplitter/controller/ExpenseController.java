package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.ExpenseDTO;
import com.expense.expensesplitter.dto.ExpenseSummaryDTO;
import com.expense.expensesplitter.model.Expense;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.service.ExpenseService;
import com.expense.expensesplitter.service.SettlementService;
import com.expense.expensesplitter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@Valid @RequestBody Expense expense) {
        ExpenseDTO createdExpense = expenseService.addExpense(expense);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
        List<ExpenseDTO> expenses = expenseService.getAllExpenses();
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryDTO> getSummary(
            @RequestParam(required = false) Long userId) {
        User currentUser = userService.getCurrentUser(userId);
        return ResponseEntity.ok(settlementService.getUserSummary(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long id) {
        ExpenseDTO expense = expenseService.getExpenseById(id);
        return new ResponseEntity<>(expense, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        List<ExpenseDTO> expenses = expenseService.getExpensesByUser(user);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expense);
        return new ResponseEntity<>(updatedExpense, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}