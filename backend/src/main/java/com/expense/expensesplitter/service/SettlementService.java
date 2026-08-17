package com.expense.expensesplitter.service;

import com.expense.expensesplitter.dto.ExpenseSummaryDTO;
import com.expense.expensesplitter.dto.SettlementDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.Expense;
import com.expense.expensesplitter.model.ExpenseShare;
import com.expense.expensesplitter.model.Settlement;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.ExpenseRepository;
import com.expense.expensesplitter.repository.ExpenseShareRepository;
import com.expense.expensesplitter.repository.SettlementRepository;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.AbstractMap;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SettlementService {

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<SettlementDTO> getAllSettlements() {
        List<Settlement> settlements = settlementRepository.findAllByOrderBySettlementDateDesc();
        return settlements.stream().map(s -> SettlementDTO.builder()
                .id(s.getId())
                .fromUserId(s.getFromUser().getId())
                .fromUserName(s.getFromUser().getName())
                .toUserId(s.getToUser().getId())
                .toUserName(s.getToUser().getName())
                .amount(s.getAmount())
                .isCompleted(s.isCompleted())
                .build()
        ).toList();
    }

    public List<SettlementDTO> getUserBalances() {
        List<Expense> expenses = expenseRepository.findAll();
        Map<Long, BigDecimal> balanceMap = new HashMap<>();

        for (Expense expense : expenses) {
            List<ExpenseShare> shares = expenseShareRepository.findByExpenseId(expense.getId());

            for (ExpenseShare share : shares) {
                Long userId = share.getUser().getId();
                BigDecimal shareAmount = share.getShareAmount();

                balanceMap.putIfAbsent(userId, BigDecimal.ZERO);

                if (expense.getPaidBy().getId().equals(share.getUser().getId())) {
                    balanceMap.put(userId, balanceMap.get(userId).add(shareAmount));
                } else {
                    balanceMap.put(userId, balanceMap.get(userId).subtract(shareAmount));
                }
            }
        }

        // Convert Map to List<SettlementDTO>
        List<SettlementDTO> balances = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : balanceMap.entrySet()) {
            User user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("User", entry.getKey()));

            SettlementDTO balance = new SettlementDTO();
            balance.setUserId(entry.getKey());
            balance.setUserName(user.getName());
            balance.setBalanceAmount(entry.getValue());
            balance.setIsCompleted(false);

            balances.add(balance);
        }

        return balances;
    }

    public List<SettlementDTO> calculateSettlements() {
        Map<Long, BigDecimal> balances = new HashMap<>();

        List<Expense> expenses = expenseRepository.findAll();
        for (Expense expense : expenses) {
            List<ExpenseShare> shares = expenseShareRepository.findByExpenseId(expense.getId());

            for (ExpenseShare share : shares) {
                Long userId = share.getUser().getId();
                BigDecimal shareAmount = share.getShareAmount();

                balances.putIfAbsent(userId, BigDecimal.ZERO);

                if (expense.getPaidBy().getId().equals(share.getUser().getId())) {
                    balances.put(userId, balances.get(userId).add(shareAmount));
                } else {
                    balances.put(userId, balances.get(userId).subtract(shareAmount));
                }
            }
        }

        List<SettlementDTO> settlements = new ArrayList<>();

        List<Map.Entry<Long, BigDecimal>> positive = new ArrayList<>();
        List<Map.Entry<Long, BigDecimal>> negative = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                positive.add(entry);
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                negative.add(entry);
            }
        }

        Collections.sort(positive, (a, b) -> b.getValue().compareTo(a.getValue()));
        Collections.sort(negative, (a, b) -> a.getValue().compareTo(b.getValue()));

        int i = 0, j = 0;
        while (i < positive.size() && j < negative.size()) {
            Long fromUserId = negative.get(j).getKey();
            Long toUserId = positive.get(i).getKey();
            BigDecimal amount = negative.get(j).getValue().abs().min(positive.get(i).getValue());

            User fromUserObj = userRepository.findById(fromUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", fromUserId));
            User toUserObj = userRepository.findById(toUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", toUserId));

            settlements.add(SettlementDTO.builder()
                    .fromUserId(fromUserId)
                    .fromUserName(fromUserObj.getName())
                    .toUserId(toUserId)
                    .toUserName(toUserObj.getName())
                    .amount(amount)
                    .isCompleted(true)
                    .build());

            BigDecimal remainingFrom = negative.get(j).getValue().add(amount);
            BigDecimal remainingTo = positive.get(i).getValue().subtract(amount);

            if (remainingFrom.compareTo(BigDecimal.ZERO) == 0) {
                j++;
            } else {
                negative.set(j, new AbstractMap.SimpleEntry<>(fromUserId, remainingFrom));
            }

            if (remainingTo.compareTo(BigDecimal.ZERO) == 0) {
                i++;
            } else {
                positive.set(i, new AbstractMap.SimpleEntry<>(toUserId, remainingTo));
            }
        }

        return settlements;
    }

    public ExpenseSummaryDTO getUserSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        BigDecimal totalExpenses = expenseRepository.findByPaidBy(user).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOwed = expenseShareRepository.calculateTotalOwed(user)
                .orElse(BigDecimal.ZERO);
        BigDecimal totalSettled = expenseShareRepository.calculateTotalSettled(user)
                .orElse(BigDecimal.ZERO);

        BigDecimal amountLent = BigDecimal.ZERO;
        List<Expense> expensesPaidByUser = expenseRepository.findByPaidBy(user);
        for (Expense expense : expensesPaidByUser) {
            List<ExpenseShare> shares = expenseShareRepository.findByExpenseId(expense.getId());
            for (ExpenseShare share : shares) {
                if (!share.getUser().getId().equals(userId) && !share.isSettled()) {
                    amountLent = amountLent.add(share.getShareAmount());
                }
            }
        }

        return ExpenseSummaryDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .totalExpenses(totalExpenses)
                .totalOwed(totalOwed)
                .totalSettled(totalSettled)
                .netBalance(totalOwed.subtract(totalSettled))
                .amountLent(amountLent)
                .build();
    }

    @Transactional
    public void markShareAsSettled(Long shareId) {
        ExpenseShare share = expenseShareRepository.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", shareId));
        share.setSettled(true);
        expenseShareRepository.save(share);
    }

    @Transactional
    public SettlementDTO markSettlementAsPaid(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", settlementId));
        settlement.setCompleted(true);
        Settlement updatedSettlement = settlementRepository.save(settlement);
        
        return SettlementDTO.builder()
                .id(updatedSettlement.getId())
                .fromUserName(updatedSettlement.getFromUser().getName())
                .toUserName(updatedSettlement.getToUser().getName())
                .amount(updatedSettlement.getAmount())
                .isCompleted(true)
                .build();
    }

    @Transactional
    public SettlementDTO processPayment(Long fromUserId, Long toUserId, Double amount) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", fromUserId));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", toUserId));

        Settlement settlement = new Settlement();
        settlement.setFromUser(fromUser);
        settlement.setToUser(toUser);
        settlement.setAmount(new BigDecimal(amount));
        settlement.setCompleted(true);
        Settlement savedSettlement = settlementRepository.save(settlement);

        return SettlementDTO.builder()
                .id(savedSettlement.getId())
                .fromUserName(fromUser.getName())
                .toUserName(toUser.getName())
                .amount(new BigDecimal(amount))
                .isCompleted(true)
                .build();
    }
}