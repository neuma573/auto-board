package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.user.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;

}
