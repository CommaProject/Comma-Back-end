package com.team.comma.follow.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.user.domain.QUser;
import com.team.comma.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.team.comma.follow.domain.QFollowing.following;

@RequiredArgsConstructor
public class FollowingRepositoryImpl implements FollowingRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    QUser user1 = new QUser("user1");
    QUser user2 = new QUser("user2");

    @Override
    public Optional<User> getFollowedMeUserByEmail(String toUserEmail , String fromUserEmail) {
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.email.eq(toUserEmail))
                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public void blockFollowedUser(String toUserEmail, String fromUserEmail) {
        queryFactory.update(following)
                .set(following.blockFlag , true)
                .where(following.id.eq(
                        JPAExpressions.select(following.id).from(following)
                                .innerJoin(following.userTo , user1).on(user1.email.eq(toUserEmail))
                                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                ))
                .execute();
    }

    @Override
    public void unblockFollowedUser(String toUserEmail, String fromUserEmail) {
        queryFactory.update(following)
                .set(following.blockFlag , false)
                .where(following.id.eq(
                        JPAExpressions.select(following.id).from(following)
                                .innerJoin(following.userTo , user1).on(user1.email.eq(toUserEmail))
                                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                ))
                .execute();
    }
}
