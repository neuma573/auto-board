package com.neuma573.autoboard.comment.repository;

import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.comment.model.entity.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.neuma573.autoboard.comment.model.entity.QComment.comment;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Comment> findParentCommentsByPostIdIncludingDeletedWithReplies(Long postId, Pageable pageable) {
        QComment subComment = new QComment("subComment");

        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.post.id.eq(postId)
                        .and(comment.parentComment.isNull())
                        .and(comment.isDeleted.isFalse()
                                .or(comment.id.in(
                                        jpaQueryFactory.select(subComment.parentComment.id)
                                                .from(subComment)
                                                .where(subComment.parentComment.id.eq(comment.id))))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.selectFrom(comment)
                .where(comment.post.id.eq(postId)
                        .and(comment.parentComment.isNull())
                        .and(comment.isDeleted.isFalse()
                                .or(comment.id.in(
                                        jpaQueryFactory.select(subComment.parentComment.id)
                                                .from(subComment)
                                                .where(subComment.parentComment.id.eq(comment.id))))))
                .fetch().size();

        return new PageImpl<>(comments, pageable, total);
    }
}
