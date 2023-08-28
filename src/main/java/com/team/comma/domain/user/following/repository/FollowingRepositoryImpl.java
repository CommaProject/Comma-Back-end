package com.team.comma.domain.user.following.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.user.following.dto.FollowingResponse;
import com.team.comma.domain.user.user.domain.QUser;
import com.team.comma.domain.user.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.team.comma.domain.user.following.domain.QFollowing.following;

@RequiredArgsConstructor
public class FollowingRepositoryImpl implements FollowingRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    QUser user1 = new QUser("user1");
    QUser user2 = new QUser("user2");

    @Override
    public Optional<User> getFollowedMeUserByEmail(long toUserId , String fromUserEmail) {
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.id.eq(toUserId))
                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                .where(following.blockFlag.eq(false))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<User> getBlockedUser(long toUserId, String fromUserEmail) {
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.id.eq(toUserId))
                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                .where(following.blockFlag.eq(true))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void blockFollowedUser(long followingId) {
        queryFactory.update(following)
                .set(following.blockFlag , true)
                .where(following.id.eq(followingId))
                .execute();
    }

    @Override
    @Transactional
    public void unblockFollowedUser(long followingId) {
        queryFactory.update(following)
                .set(following.blockFlag , false)
                .where(following.id.eq(followingId))
                .execute();
    }

    @Override
    public List<FollowingResponse> getFollowingToUserListByFromUser(User user) {
        return queryFactory.select(
                                Projections.constructor(
                                    FollowingResponse.class,
                                    following.id,
                                    following.userTo.id,
                                    following.userTo.userDetail.nickname))
                            .from(following)
                            .where(following.userFrom.eq(user)
                                    .and(following.blockFlag.eq(false)))
                            .fetch();
    }

    @Override
    public List<FollowingResponse> getFollowingFromUserListByToUser(User user) {
        return queryFactory.select(
                                Projections.constructor(
                                        FollowingResponse.class,
                                        following.id,
                                        following.userFrom.id,
                                        following.userFrom.userDetail.nickname))
                            .from(following)
                            .where(following.userTo.eq(user)
                                    .and(following.blockFlag.eq(false)))
                            .fetch();
    }

}
