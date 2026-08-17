package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.ReceiptDTO;
import com.expense.expensesplitter.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload/{expenseId}")
    public ResponseEntity<ReceiptDTO> uploadReceipt(@PathVariable Long expenseId, @RequestParam MultipartFile file) throws IOException {
        ReceiptDTO receipt = fileUploadService.uploadReceipt(expenseId, file);
        return new ResponseEntity<>(receipt, HttpStatus.CREATED);
    }

    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<List<ReceiptDTO>> getReceiptsByExpense(@PathVariable Long expenseId) {
        List<ReceiptDTO> receipts = fileUploadService.getReceiptsByExpense(expenseId);
        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptDTO> getReceiptById(@PathVariable Long id) {
        ReceiptDTO receipt = fileUploadService.getReceiptById(id);
        return new ResponseEntity<>(receipt, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        fileUploadService.deleteReceipt(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}