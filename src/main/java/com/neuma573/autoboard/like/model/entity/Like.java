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
        @UniqueConstraint(columnNames = {"created_user_id", "post_id", "is_deleted"})
})
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdBy;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public static Like of(Post post, User user) {
        return Like.builder()
                .post(post)
                .createdBy(user)
                .isDeleted(false)
                .build();
    }
}
