package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.Budget;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByUser(User user);
    
    List<Budget> findByUserAndIsActive(User user, Boolean isActive);
    
    List<Budget> findByUserAndCategory(User user, Category category);
    
    List<Budget> findByUserAndPeriod(User user, String period);
    
    Optional<Budget> findByUserAndCategoryAndPeriodAndIsActive(User user, Category category, String period, Boolean isActive);
    
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.isActive = true " +
           "AND (b.category IS NULL OR b.category = :category) " +
           "AND b.period = :period " +
           "AND (b.startDate IS NULL OR b.startDate <= :currentDate) " +
           "AND (b.endDate IS NULL OR b.endDate >= :currentDate)")
    List<Budget> findActiveBudgetsForUserAndCategoryAndPeriod(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("period") String period,
            @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.isActive = true " +
           "AND b.period = :period " +
           "AND (b.startDate IS NULL OR b.startDate <= :currentDate) " +
           "AND (b.endDate IS NULL OR b.endDate >= :currentDate)")
    List<Budget> findActiveBudgetsForUserAndPeriod(
            @Param("user") User user,
            @Param("period") String period,
            @Param("currentDate") LocalDateTime currentDate);
}