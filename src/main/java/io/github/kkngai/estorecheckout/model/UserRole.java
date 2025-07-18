package io.github.kkngai.estorecheckout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    private Long userId;
    private Integer roleId;
    private User user;
    private Role role;
} 