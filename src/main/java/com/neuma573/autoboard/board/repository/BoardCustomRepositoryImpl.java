package com.neuma573.autoboard.board.repository;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.model.entity.QBoard;
import com.neuma573.autoboard.user.model.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Board> findPublicAndNotDeletedBoardWith(User user) {
        QBoard qBoard = QBoard.board;

        BooleanBuilder conditions = new BooleanBuilder()
                .and(qBoard.isDeleted.isFalse())
                .and(qBoard.isPublic.isTrue());

        if (user != null) {
            conditions.or(qBoard.users.contains(user));
        }

        return jpaQueryFactory
                .selectFrom(qBoard)
                .where(conditions)
                .fetch();
    }

}
