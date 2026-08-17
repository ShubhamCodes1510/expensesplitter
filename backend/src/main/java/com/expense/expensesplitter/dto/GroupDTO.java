package com.expense.expensesplitter.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {

    private Long id;
    private String name;
    private String description;
    private String inviteCode;
    private Long createdById;
    private String createdByName;
    private List<UserDTO> members;
    private Long defaultCategoryId;
    private String defaultCategoryName;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int memberCount;
}
