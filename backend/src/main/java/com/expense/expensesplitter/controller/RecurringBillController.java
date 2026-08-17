package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.BillDTO;
import com.expense.expensesplitter.model.Bill;
import com.expense.expensesplitter.service.RecurringBillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-bills")
public class RecurringBillController {

    @Autowired
    private RecurringBillService recurringBillService;

    @PostMapping
    public ResponseEntity<BillDTO> createRecurringBill(@Valid @RequestBody Bill bill) {
        BillDTO createdBill = recurringBillService.createRecurringBill(bill);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllRecurringBills() {
        List<BillDTO> bills = recurringBillService.getAllRecurringBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<BillDTO>> getUpcomingRecurringBills() {
        List<BillDTO> bills = recurringBillService.getUpcomingRecurringBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getRecurringBillById(@PathVariable Long id) {
        BillDTO bill = recurringBillService.getRecurringBillById(id);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BillDTO> markRecurringBillAsPaid(@PathVariable Long id) {
        BillDTO bill = recurringBillService.markRecurringBillAsPaid(id);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringBill(@PathVariable Long id) {
        recurringBillService.deleteRecurringBill(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}