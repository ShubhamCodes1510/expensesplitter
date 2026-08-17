package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class TestController {

    @Autowired
    private SettlementService service;

    @GetMapping("/test")
    public String test(Model model) {
        // Test data
        List<?> balances = service.getUserBalances();
        model.addAttribute("balances", balances);
        model.addAttribute("message", "VS Code Auto Import Working!");
        return "test";  // templates/test.html
    }
}