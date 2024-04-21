package com.neuma573.autoboard.like.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.like.model.enums.LikeableType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "like")
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "likeable_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private LikeableType likeableType;

}
