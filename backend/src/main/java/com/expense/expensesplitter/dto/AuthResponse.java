package com.expense.expensesplitter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String email;
    private String name;
    private String username;
    private String token;
    private String tokenType;
    private String message;
}