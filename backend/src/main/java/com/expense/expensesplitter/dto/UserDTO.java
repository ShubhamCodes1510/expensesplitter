package com.expense.expensesplitter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String phone;
    private boolean isActive;
}