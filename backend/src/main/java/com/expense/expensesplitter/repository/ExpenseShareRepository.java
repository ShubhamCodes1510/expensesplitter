package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.ExpenseShare;
import com.expense.expensesplitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {

    // ✅ Find all shares by expense ID
    List<ExpenseShare> findByExpenseId(Long expenseId);

    // ✅ Find all shares by user ID
    List<ExpenseShare> findByUser_Id(Long userId);

    // ✅ FIXED: Use 'isSettled' instead of 'settled'
    List<ExpenseShare> findByUser_IdAndIsSettledFalse(Long userId);

    // ✅ FIXED: Use 'isSettled' instead of 'settled'
    List<ExpenseShare> findByUser_IdAndIsSettledTrue(Long userId);

    // ✅ FIXED: Use 'isSettled' instead of 'settled'
    List<ExpenseShare> findByIsSettledFalse();

    // ✅ FIXED: Use 'isSettled' instead of 'settled'
    List<ExpenseShare> findByIsSettledTrue();

    // ✅ Find all shares by user object
    List<ExpenseShare> findByUser(User user);

    // ✅ FIXED: Use 'isSettled' instead of 'settled'
    List<ExpenseShare> findByUserAndIsSettled(User user, boolean isSettled);

    // ✅ FIXED: Use 'isSettled' instead of 'settled'
    @Query("SELECT SUM(es.shareAmount) FROM ExpenseShare es WHERE es.user = :user AND es.isSettled = false")
    Optional<BigDecimal> calculateTotalOwed(@Param("user") User user);

    // ✅ FIXED: Use 'isSettled' instead of 'settled'
    @Query("SELECT SUM(es.shareAmount) FROM ExpenseShare es WHERE es.user = :user AND es.isSettled = true")
    Optional<BigDecimal> calculateTotalSettled(@Param("user") User user);
}