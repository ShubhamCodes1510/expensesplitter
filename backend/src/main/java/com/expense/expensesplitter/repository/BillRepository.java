package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.Bill;
import com.expense.expensesplitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByUser(User user);

    List<Bill> findByUserAndIsActive(User user, boolean isActive);

    List<Bill> findByNextDueDateBefore(LocalDateTime date);

    List<Bill> findByUserAndNextDueDateBefore(User user, LocalDateTime date);

    @Query("SELECT b FROM Bill b WHERE b.nextDueDate <= :date AND b.isActive = true")
    List<Bill> findUpcomingBills(@Param("date") LocalDateTime date);
}