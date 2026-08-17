package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.Category;
import com.expense.expensesplitter.model.Expense;
import com.expense.expensesplitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByPaidBy(User user);

    List<Expense> findByPaidByOrderByDateDesc(User user);

    List<Expense> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Expense> findByPaidByAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    List<Expense> findByPaidByAndCategoryAndDateBetween(User user, Category category, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT e FROM Expense e WHERE e.paidBy = :user AND e.date >= :startDate")
    List<Expense> findRecentExpenses(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
}