package com.neuma573.autoboard.like.repository;

import com.neuma573.autoboard.like.model.entity.Like;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.neuma573.autoboard.like.model.entity.QLike.like;

@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Like> findByCreatedByAndPostForUpdate(User user, Post post) {
        Like foundLike = jpaQueryFactory.selectFrom(like)
                .where(like.createdBy.eq(user)
                        .and(like.post.eq(post)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();

        return Optional.ofNullable(foundLike);
    }

    @Override
    public Long countByPostId(Long postId) {
        return jpaQueryFactory.select(like.count())
                .from(like)
                .where(like.post.id.eq(postId))
                .fetchOne();
    }

}
