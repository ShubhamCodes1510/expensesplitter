package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.ReceiptDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.Expense;
import com.expense.expensesplitter.model.Receipt;
import com.expense.expensesplitter.repository.ExpenseRepository;
import com.expense.expensesplitter.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class FileUploadService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Value("${app.upload.path:uploads/}")
    private String uploadDir;

    public ReceiptDTO uploadReceipt(Long expenseId, MultipartFile file) throws IOException {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", expenseId));

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Receipt receipt = new Receipt();
        receipt.setExpense(expense);
        receipt.setFileName(newFilename);
        receipt.setFilePath(filePath.toString());
        receipt.setFileType(file.getContentType());
        receipt.setFileSize(file.getSize());

        Receipt savedReceipt = receiptRepository.save(receipt);

        return ReceiptDTO.builder()
                .id(savedReceipt.getId())
                .fileName(savedReceipt.getFileName())
                .filePath(savedReceipt.getFilePath())
                .fileType(savedReceipt.getFileType())
                .fileSize(savedReceipt.getFileSize())
                .uploadUrl("/api/files/" + savedReceipt.getId())
                .build();
    }

    public List<ReceiptDTO> getReceiptsByExpense(Long expenseId) {
        return receiptRepository.findByExpenseId(expenseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReceiptDTO getReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", id));
        return mapToDTO(receipt);
    }

    public void deleteReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", id));
        try {
            Files.deleteIfExists(Paths.get(receipt.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
        receiptRepository.delete(receipt);
    }

    private ReceiptDTO mapToDTO(Receipt receipt) {
        return ReceiptDTO.builder()
                .id(receipt.getId())
                .fileName(receipt.getFileName())
                .filePath(receipt.getFilePath())
                .fileType(receipt.getFileType())
                .fileSize(receipt.getFileSize())
                .uploadUrl("/api/files/" + receipt.getId())
                .build();
    }
}