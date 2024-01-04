package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.HashSet;
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

    @Column(name = "login_id", unique = true)
    private String loginId;

    private String name;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String password;

    private Long failCount;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();

    public UserResponse toResponse() {
        return UserResponse.builder()
                .loginId(this.loginId)
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
}
