package com.neuma573.autoboard.post.model.entity;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.post.model.enums.PostAction;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "post_history")
public class PostHistory  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private User changedBy;

    @CreatedDate
    @Column(name = "changed_at")
    private LocalDateTime changedAt;

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

    private Long views;

    private boolean isDeleted;

    @Column(name = "history_type")
    @Enumerated(EnumType.STRING)
    private PostAction historyType;
}
