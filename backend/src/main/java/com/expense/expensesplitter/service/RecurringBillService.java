package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.BillDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.Bill;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.BillRepository;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecurringBillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BillDTO createRecurringBill(Bill bill) {
        User user = userRepository.findById(bill.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", bill.getUser().getId()));
        bill.setUser(user);

        if (bill.getNextDueDate() == null) {
            bill.setNextDueDate(LocalDateTime.now());
        }

        Bill savedBill = billRepository.save(bill);
        return mapToDTO(savedBill);
    }

    public BillDTO getRecurringBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
        return mapToDTO(bill);
    }

    public List<BillDTO> getAllRecurringBills() {
        return billRepository.findAll().stream()
                .filter(Bill::isActive)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BillDTO> getUpcomingRecurringBills() {
        return billRepository.findUpcomingBills(LocalDateTime.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BillDTO markRecurringBillAsPaid(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));

        // Calculate next due date based on frequency
        LocalDateTime nextDueDate = calculateNextDueDate(bill.getNextDueDate(), bill.getFrequency());
        bill.setNextDueDate(nextDueDate);

        Bill updatedBill = billRepository.save(bill);
        return mapToDTO(updatedBill);
    }

    @Transactional
    public void deleteRecurringBill(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
        bill.setActive(false);
        billRepository.save(bill);
    }

    private LocalDateTime calculateNextDueDate(LocalDateTime currentDueDate, String frequency) {
        LocalDateTime nextDate = currentDueDate;

        switch (frequency.toUpperCase()) {
            case "DAILY":
                nextDate = nextDate.plusDays(1);
                break;
            case "WEEKLY":
                nextDate = nextDate.plusWeeks(1);
                break;
            case "MONTHLY":
                nextDate = nextDate.plusMonths(1);
                break;
            case "YEARLY":
                nextDate = nextDate.plusYears(1);
                break;
            default:
                nextDate = nextDate.plusDays(30);
        }

        return nextDate;
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