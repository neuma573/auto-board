package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.global.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Audited
@Entity
@Table(name = "user", indexes = {@Index(name = "idx_user_email", columnList = "email", unique = true)})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    private String password;

    @NotAudited
    @Builder.Default
    private Long failCount = 0L;

    @NotAudited
    private LocalDateTime lastLoginAt;

    private LocalDateTime lastPasswordChangedAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<UserRole> roles;

    private String picture;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotAudited
    private List<AuthenticationProvider> authenticationProviders;

    public void addRole(UserRole userRole){
        roles.add(userRole);
    }

    public void addFailCount() {
        failCount++;
    }

    public void setLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public String getStatus() {
        return status.getStatus();
    }

    public boolean shouldChangePassword() {
        return ChronoUnit.DAYS.between(lastPasswordChangedAt, LocalDateTime.now()) > 180;
    }
}
