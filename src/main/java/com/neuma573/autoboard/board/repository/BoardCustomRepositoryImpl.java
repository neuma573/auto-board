package com.neuma573.autoboard.board.repository;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.model.entity.QBoard;
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

        BooleanBuilder conditions = new BooleanBuilder()
                .and(qBoard.isDeleted.isFalse())
                .and(qBoard.isPublic.isTrue());

        userOptional.ifPresent(user -> conditions.or(qBoard.users.contains(user)));

        return jpaQueryFactory
                .selectFrom(qBoard)
                .where(conditions)
                .fetch();
    }

}
