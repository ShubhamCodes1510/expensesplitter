package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByExpenseId(Long expenseId);

    List<Receipt> findByBillId(Long billId);

    List<Receipt> findByExpenseIdOrderByUploadedAtDesc(Long expenseId);
}