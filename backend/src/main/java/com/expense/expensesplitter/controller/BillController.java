package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.BillDTO;
import com.expense.expensesplitter.model.Bill;
import com.expense.expensesplitter.service.BillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping
    public ResponseEntity<BillDTO> createBill(@Valid @RequestBody Bill bill) {
        BillDTO createdBill = billService.addBill(bill);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<BillDTO> bills = billService.getAllBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
        BillDTO bill = billService.getBillById(id);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<BillDTO>> getUpcomingBills() {
        List<BillDTO> bills = billService.getUpcomingBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BillDTO> markBillAsPaid(@PathVariable Long id) {
        BillDTO bill = billService.markBillAsPaid(id);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}