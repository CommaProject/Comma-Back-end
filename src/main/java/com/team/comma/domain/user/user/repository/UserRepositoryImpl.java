package com.team.comma.domain.user.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.team.comma.domain.user.user.domain.QUser.user;
import static com.team.comma.domain.user.userDetail.QUserDetail.userDetail;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> searchUserByUserNameAndNickName(String name) {
        return queryFactory.select(user).from(user).leftJoin(user.userDetail).fetchJoin()
                .where(userDetail.nickname.eq(name)
                        .or(userDetail.name.eq(name)))
                .fetch();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User result = queryFactory.select(user)
                .from(user)
                .leftJoin(user.userDetail)
                .fetchJoin()
                .where(user.email.eq(email))
                .fetchFirst();

        return Optional.ofNullable(result);
    }
}
