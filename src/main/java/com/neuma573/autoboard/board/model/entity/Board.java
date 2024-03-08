package com.neuma573.autoboard.board.model.entity;

import com.neuma573.autoboard.global.model.entity.BaseEntity;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Audited
@Table
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @NotAudited
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "board_users",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    private boolean isPublic;

    private boolean isDeleted;

}