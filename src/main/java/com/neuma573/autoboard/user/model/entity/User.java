package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.user.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@RequiredArgsConstructor
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

    private String status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LinkedAccount> linkedAccounts;


}
