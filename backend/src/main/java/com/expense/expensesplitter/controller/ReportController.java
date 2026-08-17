package com.expense.expensesplitter.controller;

import com.expense.expensesplitter.dto.ExpenseDTO;
import com.expense.expensesplitter.dto.SettlementDTO;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.SettlementRepository;
import com.expense.expensesplitter.repository.UserRepository;
import com.expense.expensesplitter.service.ExpenseService;
import com.expense.expensesplitter.service.SettlementService;
import com.expense.expensesplitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @GetMapping("/expenses/csv")
    public ResponseEntity<byte[]> exportExpensesCSV(@RequestParam(required = false) Long userId) {
        List<ExpenseDTO> expenses;
        if (userId != null) {
            // Get user object first
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));
            expenses = expenseService.getExpensesByUser(user);
        } else {
            expenses = expenseService.getAllExpenses();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        // Write CSV header
        writer.println("ID,Description,Amount,Category,Paid By,Date,Status");

        // Write data
        for (ExpenseDTO expense : expenses) {
            writer.printf("%d,\"%s\",%.2f,\"%s\",\"%s\",%s,%s%n",
                    expense.getId(),
                    escapeCsv(expense.getDescription()),
                    expense.getAmount().doubleValue(),
                    escapeCsv(expense.getCategoryName()),
                    escapeCsv(expense.getPaidByName()),
                    expense.getDate(),
                    "Active");
        }

        writer.flush();
        byte[] csvBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "expenses_" + LocalDate.now() + ".csv");
        headers.setContentLength(csvBytes.length);

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/settlements/csv")
    public ResponseEntity<byte[]> exportSettlementsCSV(@RequestParam(required = false) Long userId) {
        List<SettlementDTO> settlements;
        if (userId != null) {
            // For now, get all settlements and filter by user
            settlements = settlementRepository.findAll().stream()
                    .map(settlement -> SettlementDTO.builder()
                            .id(settlement.getId())
                            .fromUserId(settlement.getFromUser().getId())
                            .fromUserName(settlement.getFromUser().getName())
                            .toUserId(settlement.getToUser().getId())
                            .toUserName(settlement.getToUser().getName())
                            .amount(settlement.getAmount())
                            .isCompleted(settlement.isCompleted())
                            .build())
                    .filter(s -> s.getFromUserId().equals(userId) || s.getToUserId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            settlements = settlementRepository.findAll().stream()
                    .map(settlement -> SettlementDTO.builder()
                            .id(settlement.getId())
                            .fromUserId(settlement.getFromUser().getId())
                            .fromUserName(settlement.getFromUser().getName())
                            .toUserId(settlement.getToUser().getId())
                            .toUserName(settlement.getToUser().getName())
                            .amount(settlement.getAmount())
                            .isCompleted(settlement.isCompleted())
                            .build())
                    .collect(Collectors.toList());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        // Write CSV header
        writer.println("ID,From User,To User,Amount,Status,Date");

        // Write data
        for (SettlementDTO settlement : settlements) {
            writer.printf("%d,\"%s\",\"%s\",%.2f,%s,%s%n",
                    settlement.getId(),
                    escapeCsv(settlement.getFromUserName()),
                    escapeCsv(settlement.getToUserName()),
                    settlement.getAmount().doubleValue(),
                    settlement.getIsCompleted() ? "Completed" : "Pending",
                    "N/A"); // No createdAt in DTO
        }

        writer.flush();
        byte[] csvBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "settlements_" + LocalDate.now() + ".csv");
        headers.setContentLength(csvBytes.length);

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/summary/html")
    public ResponseEntity<String> getSummaryHTML() {
        List<ExpenseDTO> expenses = expenseService.getAllExpenses();
        
        // Get settlements from repository
        List<SettlementDTO> settlements = settlementRepository.findAll().stream()
                .map(settlement -> SettlementDTO.builder()
                        .id(settlement.getId())
                        .fromUserId(settlement.getFromUser().getId())
                        .fromUserName(settlement.getFromUser().getName())
                        .toUserId(settlement.getToUser().getId())
                        .toUserName(settlement.getToUser().getName())
                        .amount(settlement.getAmount())
                        .isCompleted(settlement.isCompleted())
                        .build())
                .collect(Collectors.toList());

        double totalExpenses = expenses.stream().mapToDouble(e -> e.getAmount().doubleValue()).sum();
        double totalSettlements = settlements.stream().mapToDouble(s -> s.getAmount().doubleValue()).sum();
        long pendingSettlements = settlements.stream().filter(s -> !s.getIsCompleted()).count();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<title>Expense Splitter Report</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; }\n");
        html.append("h1 { color: #333; }\n");
        html.append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }\n");
        html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n");
        html.append("th { background-color: #f2f2f2; }\n");
        html.append(".summary { background-color: #f9f9f9; padding: 20px; border-radius: 5px; margin-bottom: 20px; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>Expense Splitter Report</h1>\n");
        html.append("<div class=\"summary\">\n");
        html.append("<h2>Summary</h2>\n");
        html.append("<p><strong>Total Expenses:</strong> ₹").append(String.format("%.2f", totalExpenses)).append("</p>\n");
        html.append("<p><strong>Total Settlements:</strong> ₹").append(String.format("%.2f", totalSettlements)).append("</p>\n");
        html.append("<p><strong>Pending Settlements:</strong> ").append(pendingSettlements).append("</p>\n");
        html.append("<p><strong>Report Date:</strong> ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>\n");
        html.append("</div>\n");

        // Expenses table
        html.append("<h2>Recent Expenses</h2>\n");
        html.append("<table>\n");
        html.append("<tr><th>ID</th><th>Description</th><th>Amount</th><th>Category</th><th>Paid By</th><th>Date</th></tr>\n");
        expenses.stream().limit(10).forEach(expense -> {
            html.append("<tr>");
            html.append("<td>").append(expense.getId()).append("</td>");
            html.append("<td>").append(escapeHtml(expense.getDescription())).append("</td>");
            html.append("<td>₹").append(String.format("%.2f", expense.getAmount().doubleValue())).append("</td>");
            html.append("<td>").append(escapeHtml(expense.getCategoryName())).append("</td>");
            html.append("<td>").append(escapeHtml(expense.getPaidByName())).append("</td>");
            html.append("<td>").append(expense.getDate()).append("</td>");
            html.append("</tr>\n");
        });
        html.append("</table>\n");

        // Settlements table
        html.append("<h2>Recent Settlements</h2>\n");
        html.append("<table>\n");
        html.append("<tr><th>ID</th><th>From</th><th>To</th><th>Amount</th><th>Status</th><th>Date</th></tr>\n");
        settlements.stream().limit(10).forEach(settlement -> {
            html.append("<tr>");
            html.append("<td>").append(settlement.getId()).append("</td>");
            html.append("<td>").append(escapeHtml(settlement.getFromUserName())).append("</td>");
            html.append("<td>").append(escapeHtml(settlement.getToUserName())).append("</td>");
            html.append("<td>₹").append(String.format("%.2f", settlement.getAmount().doubleValue())).append("</td>");
            html.append("<td>").append(settlement.getIsCompleted() ? "Completed" : "Pending").append("</td>");
            html.append("<td>N/A</td>");
            html.append("</tr>\n");
        });
        html.append("</table>\n");

        html.append("</body>\n");
        html.append("</html>");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<>(html.toString(), headers, HttpStatus.OK);
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return input.replace("\"", "\"\"");
    }

    private String escapeHtml(String input) {
    if (input == null) return "";
    return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
}
}