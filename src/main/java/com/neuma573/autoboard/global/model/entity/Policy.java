package com.neuma573.autoboard.global.model.entity;


import com.neuma573.autoboard.user.model.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "policy")
@Getter
@Entity
public class Policy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String policyName;

    @Column(columnDefinition = "TEXT")
    private String policy;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

}
