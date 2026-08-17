package com.expense.expensesplitter.repository;

import com.expense.expensesplitter.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUser_IdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId")
    void markAsRead(Long notificationId);
}
