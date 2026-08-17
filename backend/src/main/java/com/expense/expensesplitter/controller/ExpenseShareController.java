package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.ExpenseShareDTO;
import com.expense.expensesplitter.model.ExpenseShare;
import com.expense.expensesplitter.service.ExpenseShareService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense-shares")
public class ExpenseShareController {

    @Autowired
    private ExpenseShareService expenseShareService;

    @PostMapping
    public ResponseEntity<ExpenseShareDTO> createExpenseShare(@Valid @RequestBody ExpenseShare expenseShare) {
        ExpenseShareDTO createdShare = expenseShareService.createExpenseShare(expenseShare);
        return new ResponseEntity<>(createdShare, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseShareDTO>> getAllExpenseShares() {
        List<ExpenseShareDTO> shares = expenseShareService.getAllExpenseShares();
        return new ResponseEntity<>(shares, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseShareDTO> getExpenseShareById(@PathVariable Long id) {
        ExpenseShareDTO share = expenseShareService.getExpenseShareById(id);
        return new ResponseEntity<>(share, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseShareDTO> updateExpenseShare(@PathVariable Long id, @Valid @RequestBody ExpenseShare expenseShare) {
        ExpenseShareDTO updatedShare = expenseShareService.updateExpenseShare(id, expenseShare);
        return new ResponseEntity<>(updatedShare, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseShare(@PathVariable Long id) {
        expenseShareService.deleteExpenseShare(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<List<ExpenseShareDTO>> getSharesByExpense(@PathVariable Long expenseId) {
        List<ExpenseShareDTO> shares = expenseShareService.getSharesByExpense(expenseId);
        return new ResponseEntity<>(shares, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseShareDTO>> getSharesByUser(@PathVariable Long userId) {
        List<ExpenseShareDTO> shares = expenseShareService.getSharesByUser(userId);
        return new ResponseEntity<>(shares, HttpStatus.OK);
    }

    @GetMapping("/unsettled/user/{userId}")
    public ResponseEntity<List<ExpenseShareDTO>> getUnsettledSharesByUser(@PathVariable Long userId) {
        List<ExpenseShareDTO> shares = expenseShareService.getUnsettledSharesByUser(userId);
        return ResponseEntity.ok(shares);
    }
}