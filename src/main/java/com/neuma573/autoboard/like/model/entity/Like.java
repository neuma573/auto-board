package com.neuma573.autoboard.like.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "created_user_id"})
})
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdBy;

    public static Like of(Post post, User user) {
        return Like.builder()
                .post(post)
                .createdBy(user)
                .build();
    }
}
