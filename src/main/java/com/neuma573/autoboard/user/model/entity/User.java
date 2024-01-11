package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String password;

    private Long failCount = 0L;

    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<UserRole> roles;

    public UserResponse toResponse() {
        return UserResponse.builder()
                .email(this.email)
                .name(this.name)
                .build();
    }

    public void addRole(UserRole userRole){
        roles.add(userRole);
    }

    public void addFailCount() {
        failCount++;
    }

    public void setLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
