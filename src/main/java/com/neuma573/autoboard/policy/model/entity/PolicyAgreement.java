package com.neuma573.autoboard.policy.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "policy_agreement")
@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PolicyAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy agreedFor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User agreedBy;

    @Column(nullable = false)
    private boolean isAgreed;

    @Column(nullable = false)
    private LocalDateTime agreedAt;
}