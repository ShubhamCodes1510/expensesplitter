package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.ExpenseSummaryDTO;
import com.expense.expensesplitter.dto.SettlementDTO;
import com.expense.expensesplitter.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    @GetMapping
    public ResponseEntity<List<SettlementDTO>> getAllSettlements() {
        List<SettlementDTO> settlements = settlementService.getAllSettlements();
        return ResponseEntity.ok(settlements);
    }

    @GetMapping("/calculate")
    public ResponseEntity<List<SettlementDTO>> calculateSettlements() {
        List<SettlementDTO> settlements = settlementService.calculateSettlements();
        return ResponseEntity.ok(settlements);
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<ExpenseSummaryDTO> getUserSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(settlementService.getUserSummary(userId));
    }

    @GetMapping("/balances")
    public ResponseEntity<List<SettlementDTO>> getUserBalances() {
        List<SettlementDTO> balances = settlementService.getUserBalances();
        return ResponseEntity.ok(balances);
    }

    @PostMapping("/{settlementId}/pay")
    public ResponseEntity<SettlementDTO> markAsPaid(@PathVariable Long settlementId) {
        SettlementDTO settlement = settlementService.markSettlementAsPaid(settlementId);
        return ResponseEntity.ok(settlement);
    }

    @PostMapping("/process-payment")
    public ResponseEntity<SettlementDTO> processPayment(@RequestBody Map<String, Object> paymentRequest) {
        Object fromObj = paymentRequest.get("fromUserId");
        Object toObj = paymentRequest.get("toUserId");
        Object amountObj = paymentRequest.get("amount");
        if (fromObj == null || toObj == null || amountObj == null) {
            return ResponseEntity.badRequest().build();
        }
        Long fromUserId = ((Number) fromObj).longValue();
        Long toUserId = ((Number) toObj).longValue();
        Double amount = ((Number) amountObj).doubleValue();
        
        SettlementDTO settlement = settlementService.processPayment(fromUserId, toUserId, amount);
        return ResponseEntity.ok(settlement);
    }
}