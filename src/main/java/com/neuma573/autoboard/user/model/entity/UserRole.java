package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.user.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_role")
public class UserRole {
    @Id
    @Column(name = "user_role_id", length = 50)
    private Long userRoleId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Role role;

}
