package com.neuma573.autoboard.user.repository;

import com.neuma573.autoboard.user.model.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void resetAllUserFailCounts() {
        QUser user = QUser.user;
        jpaQueryFactory.update(user)
                .set(user.failCount, 0L)
                .execute();
    }
}
