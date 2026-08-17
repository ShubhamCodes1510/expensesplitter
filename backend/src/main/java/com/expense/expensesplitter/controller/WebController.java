package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.SettlementDTO;
import com.expense.expensesplitter.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private SettlementService settlementService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<SettlementDTO> balances = settlementService.getUserBalances();
        model.addAttribute("balances", balances);
        return "dashboard";  // templates/dashboard.html
    }

    @GetMapping("/settlements")
    public String settlements(Model model) {
        List<SettlementDTO> settlements = settlementService.calculateSettlements();
        model.addAttribute("settlements", settlements);
        model.addAttribute("balances", settlementService.getUserBalances());
        return "settlements";
    }

    @GetMapping("/expenses")
    public String expenses(Model model) {
        // Add expense list logic here
        return "expenses";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}