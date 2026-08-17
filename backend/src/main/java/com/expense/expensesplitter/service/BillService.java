package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.BillDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.Bill;
import com.expense.expensesplitter.model.Category;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.BillRepository;
import com.expense.expensesplitter.repository.CategoryRepository;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public BillDTO addBill(Bill bill) {
        User user = userRepository.findById(bill.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", bill.getUser().getId()));
        bill.setUser(user);

        if (bill.getCategory() != null && bill.getCategory().getId() != null) {
            Category category = categoryRepository.findById(bill.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", bill.getCategory().getId()));
            bill.setCategory(category);
        }

        if (bill.getNextDueDate() == null) {
            bill.setNextDueDate(LocalDateTime.now().plusDays(30));
        }

        Bill savedBill = billRepository.save(bill);
        return mapToDTO(savedBill);
    }

    public BillDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
        return mapToDTO(bill);
    }

    public List<BillDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BillDTO> getUpcomingBills() {
        return billRepository.findUpcomingBills(LocalDateTime.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BillDTO markBillAsPaid(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
        bill.setNextDueDate(LocalDateTime.now().plusDays(30));
        Bill updatedBill = billRepository.save(bill);
        return mapToDTO(updatedBill);
    }

    @Transactional
    public void deleteBill(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
        billRepository.delete(bill);
    }

    private BillDTO mapToDTO(Bill bill) {
        return BillDTO.builder()
                .id(bill.getId())
                .userId(bill.getUser().getId())
                .userName(bill.getUser().getName())
                .categoryId(bill.getCategory() != null ? bill.getCategory().getId() : null)
                .categoryName(bill.getCategory() != null ? bill.getCategory().getName() : null)
                .description(bill.getDescription())
                .amount(bill.getAmount())
                .frequency(bill.getFrequency())
                .nextDueDate(bill.getNextDueDate())
                .isActive(bill.isActive())
                .build();
    }
}