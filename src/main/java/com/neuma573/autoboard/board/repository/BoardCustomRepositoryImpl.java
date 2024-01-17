package com.neuma573.autoboard.board.repository;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.model.entity.QBoard;
import com.neuma573.autoboard.user.model.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Board> findPublicAndNotDeletedBoardWith(User user) {
        QBoard qBoard = QBoard.board;

        return jpaQueryFactory
                .selectFrom(qBoard)
                .where(qBoard.isDeleted.isFalse(),
                        qBoard.isPublic.isTrue(),
                        qBoard.users.contains(user))
                .fetch();
    }
}
