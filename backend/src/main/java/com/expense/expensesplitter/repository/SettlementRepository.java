package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.Settlement;
import com.expense.expensesplitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findByFromUser(User fromUser);

    List<Settlement> findByToUser(User toUser);

    List<Settlement> findByFromUserAndToUser(User fromUser, User toUser);

    List<Settlement> findAllByOrderBySettlementDateDesc();

    List<Settlement> findByFromUserOrToUserOrderBySettlementDateDesc(User fromUser, User toUser);

    @Query("SELECT SUM(s.amount) FROM Settlement s WHERE s.fromUser = :user")
    BigDecimal calculateTotalPaid(@Param("user") User user);

    @Query("SELECT SUM(s.amount) FROM Settlement s WHERE s.toUser = :user")
    BigDecimal calculateTotalReceived(@Param("user") User user);
}