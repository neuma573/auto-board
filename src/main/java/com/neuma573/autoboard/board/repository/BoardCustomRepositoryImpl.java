package com.neuma573.autoboard.board.repository;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.model.entity.QBoard;
import com.neuma573.autoboard.post.model.entity.QPost;
import com.neuma573.autoboard.user.model.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Board> findPublicAndNotDeletedBoardWith(Optional<User> userOptional) {
        QBoard qBoard = QBoard.board;
        QPost qPost = QPost.post;

        BooleanBuilder conditions = new BooleanBuilder()
                .and(qBoard.isDeleted.isFalse())
                .and(qBoard.isPublic.isTrue())
                .and(qPost.isDeleted.isFalse());

        userOptional.ifPresent(user -> conditions.and(qBoard.users.contains(user)));

        return jpaQueryFactory
                .selectFrom(qBoard)
                .leftJoin(qBoard.posts, qPost)
                .where(conditions)
                .fetch();
    }

}
