package io.github.kkngai.estorecheckout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "role_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RolePermission.RolePermissionId.class)
public class RolePermission {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "permission_id", referencedColumnName = "permission_id")
    private Permission permission;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolePermissionId implements Serializable {
        private Integer role;
        private Integer permission;
    }
} 