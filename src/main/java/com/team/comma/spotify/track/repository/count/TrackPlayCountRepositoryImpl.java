package com.team.comma.spotify.track.repository.count;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.follow.domain.QFollowing;
import com.team.comma.spotify.track.domain.TrackPlayCount;
import com.team.comma.user.domain.QUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.team.comma.spotify.track.domain.QTrackPlayCount.trackPlayCount;
import static com.team.comma.user.domain.QUser.user;


@RequiredArgsConstructor
public class TrackPlayCountRepositoryImpl implements CustomTrackPlayCount {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<TrackPlayCount> findTrackPlayCountByUserEmail(String userEmail , String trackId) {
        TrackPlayCount result = queryFactory.select(trackPlayCount).from(trackPlayCount)
                .join(trackPlayCount.user , user).on(user.email.eq(userEmail))
                .where(trackPlayCount.trackId.eq(trackId)).fetchOne();

        return Optional.of(result);
    }

    @Override
    public List<TrackPlayCount> findTrackPlayCountByFriend(String userEmail) {
        QFollowing following = new QFollowing("following");
        QUser fromUser = new QUser("fromUser");

        return queryFactory.select(trackPlayCount).from(trackPlayCount)
                .join(trackPlayCount.user , user).on(user.eqAny(
                        JPAExpressions.select(following.userTo).from(following)
                                .join(following.userFrom , fromUser).on(fromUser.email.eq(userEmail)))
                )
                .orderBy(trackPlayCount.playCount.desc())
                .limit(20)
                .fetch();
    }
}
