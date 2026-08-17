package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.NotificationDTO;
import com.expense.expensesplitter.model.Notification;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.NotificationRepository;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EmailService emailService;

    @Transactional
    public NotificationDTO createNotification(Long userId, String title, String message,
                                             Notification.NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationDTO dto = mapToDTO(saved);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications",
                dto
        );

        // Send email notification based on type
        sendEmailNotification(user, title, message, type, null, null);

        return dto;
    }

    @Transactional
    public NotificationDTO createNotification(Long userId, String title, String message,
                                             Notification.NotificationType type,
                                             Long referenceId, String referenceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationDTO dto = mapToDTO(saved);

        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications",
                dto
        );

        // Send email notification based on type
        sendEmailNotification(user, title, message, type, referenceId, referenceType);

        return dto;
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void notifyExpenseCreated(Long userId, String expenseDescription, Double amount) {
        createNotification(
                userId,
                "New Expense Added",
                expenseDescription + " - $" + amount + " has been added",
                Notification.NotificationType.EXPENSE_ADDED
        );
    }

    @Transactional
    public void notifySettlementDue(Long userId, String fromUser, Double amount) {
        createNotification(
                userId,
                "Settlement Due",
                "You owe " + fromUser + " $" + amount,
                Notification.NotificationType.SETTLEMENT_DUE
        );
    }

    @Transactional
    public void notifyPaymentReceived(Long userId, String fromUser, Double amount) {
        createNotification(
                userId,
                "Payment Received",
                fromUser + " paid you $" + amount,
                Notification.NotificationType.PAYMENT_RECEIVED
        );
    }

    @Transactional
    public void notifyGroupInvite(Long userId, String groupName, String invitedBy) {
        createNotification(
                userId,
                "Group Invitation",
                invitedBy + " invited you to join " + groupName,
                Notification.NotificationType.GROUP_INVITE
        );
    }

    private void sendEmailNotification(User user, String title, String message,
                                      Notification.NotificationType type,
                                      Long referenceId, String referenceType) {
        try {
            switch (type) {
                case EXPENSE_ADDED:
                    // Parse expense details from message
                    String[] expenseParts = message.split(" - \\$");
                    if (expenseParts.length >= 2) {
                        String expenseDesc = expenseParts[0];
                        String amountStr = expenseParts[1].split(" ")[0];
                        Double amount = Double.parseDouble(amountStr);
                        // Extract paid by from message if available
                        String paidBy = "Another user";
                        if (message.contains("paid by")) {
                            paidBy = message.substring(message.indexOf("paid by") + 8);
                        }
                        emailService.sendExpenseNotification(user, expenseDesc, amount, paidBy);
                    }
                    break;
                case SETTLEMENT_DUE:
                    // Parse settlement details
                    if (message.contains("You owe ")) {
                        String[] parts = message.split("You owe | \\$");
                        if (parts.length >= 3) {
                            String fromUser = parts[1];
                            String amountStr = parts[2];
                            Double amount = Double.parseDouble(amountStr);
                            emailService.sendSettlementNotification(user, fromUser, amount, false);
                        }
                    }
                    break;
                case PAYMENT_RECEIVED:
                    // Parse payment details
                    if (message.contains(" paid you \\$")) {
                        String[] parts = message.split(" paid you \\$");
                        if (parts.length >= 2) {
                            String fromUser = parts[0];
                            String amountStr = parts[1];
                            Double amount = Double.parseDouble(amountStr);
                            emailService.sendSettlementNotification(user, fromUser, amount, true);
                        }
                    }
                    break;
                case GROUP_INVITE:
                    // Parse group invite details
                    if (message.contains(" invited you to join ")) {
                        String[] parts = message.split(" invited you to join ");
                        if (parts.length >= 2) {
                            String invitedBy = parts[0];
                            String groupName = parts[1];
                            emailService.sendGroupInviteNotification(user, groupName, invitedBy);
                        }
                    }
                    break;
                default:
                    // For other notification types, send a generic email
                    if (Boolean.TRUE.equals(user.getEmailNotificationsEnabled())) {
                        Map<String, Object> variables = new HashMap<>();
                        variables.put("userName", user.getName());
                        variables.put("title", title);
                        variables.put("message", message);
                        variables.put("type", type.name());
                        
                        emailService.sendEmail(
                            user.getEmail(),
                            title,
                            "generic-notification",
                            variables
                        );
                    }
                    break;
            }
        } catch (Exception e) {
            // Log error but don't fail the notification creation
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType().name())
                .isRead(notification.isRead())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
