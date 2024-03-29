package com.neuma573.autoboard.post.model.entity;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Audited
@Table(name = "post")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Setter
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user_id")
    private User createdBy;

    @NotAudited
    private Long views;

    private boolean isDeleted;

    @NotAudited
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;


    public void addViews() {
        views++;
    }

    @Override
    public void delete() {
        this.isDeleted = true;
        super.delete();
    }

    public Long getActiveCommentCount() {
        return comments == null
                ? 0L
                : comments.stream()
                .filter(comment -> !comment.isDeleted())
                .count();
    }

    @Transient
    @Setter
    private User currentUser;

}
