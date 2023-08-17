package com.team.comma.domain.spotify.track.repository.count;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.follow.domain.QFollowing;
import com.team.comma.domain.spotify.track.domain.TrackPlayCount;
import com.team.comma.domain.spotify.track.dto.TrackPlayCountResponse;
import com.team.comma.domain.user.domain.QUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.team.comma.domain.spotify.track.domain.QTrackPlayCount.trackPlayCount;
import static com.team.comma.domain.user.domain.QUser.user;

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
    public List<TrackPlayCountResponse> findTrackPlayCountByFriend(String userEmail) {
        QFollowing following = new QFollowing("following");
        QUser fromUser = new QUser("fromUser");

        return queryFactory.select(Projections.constructor(
                        TrackPlayCountResponse.class,
                        trackPlayCount.playCount,
                        trackPlayCount.trackId,
                        trackPlayCount.trackImageUrl,
                        trackPlayCount.trackName,
                        trackPlayCount.trackArtist

                )).from(trackPlayCount)
                .join(trackPlayCount.user , user).on(user.eqAny(
                        JPAExpressions.select(following.userTo).from(following)
                                .join(following.userFrom , fromUser).on(fromUser.email.eq(userEmail)))
                )
                .orderBy(trackPlayCount.playCount.desc())
                .limit(20)
                .fetch();
    }

    @Override
    public List<TrackPlayCountResponse> findTrackPlayCountByMostListenedTrack(String userEmail) {
        return queryFactory.select(Projections.constructor(
                        TrackPlayCountResponse.class,
                        trackPlayCount.playCount,
                        trackPlayCount.trackId,
                        trackPlayCount.trackImageUrl,
                        trackPlayCount.trackName,
                        trackPlayCount.trackArtist

                )).from(trackPlayCount)
                .join(trackPlayCount.user , user).on(user.email.eq(userEmail))
                .limit(20)
                .orderBy(trackPlayCount.playCount.desc())
                .fetch();
    }
}
