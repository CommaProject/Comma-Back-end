package com.team.comma.domain.track.playcount.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.playcount.dto.TrackPlayCountResponse;
import com.team.comma.domain.track.playcount.domain.TrackPlayCount;

import com.team.comma.domain.track.track.dto.TrackResponse;
import com.team.comma.domain.user.following.domain.QFollowing;
import com.team.comma.domain.user.user.domain.QUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.ExpressionUtils.count;
import static com.team.comma.domain.artist.domain.QArtist.artist;
import static com.team.comma.domain.track.artist.domain.QTrackArtist.trackArtist;
import static com.team.comma.domain.track.track.domain.QTrack.track;
import static com.team.comma.domain.track.playcount.domain.QTrackPlayCount.trackPlayCount;
import static com.team.comma.domain.user.user.domain.QUser.user;

@RequiredArgsConstructor
public class TrackPlayCountRepositoryImpl implements TrackPlayCountRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TrackPlayCountResponse> findTrackPlayCountByFriend(String userEmail) {
        QFollowing following = new QFollowing("following");
        QUser fromUser = new QUser("fromUser");
        NumberPath<Long> aliasCount = Expressions.numberPath(Long.class, "count");

        return queryFactory.select(Projections.constructor(
                        TrackPlayCountResponse.class,
                        trackPlayCount.count().as(aliasCount),
                        Projections.constructor(TrackArtistResponse.class,
                                Projections.constructor(TrackResponse.class,
                                        track.id,
                                        track.trackTitle,
                                        track.durationTimeMs,
                                        track.recommendCount,
                                        track.albumImageUrl,
                                        track.spotifyTrackId,
                                        track.spotifyTrackHref
                                ),
                                Projections.constructor(ArtistResponse.class,
                                        artist.spotifyArtistId.min(),
                                        artist.artistName.min(),
                                        artist.artistImageUrl.min()
                                )
                        )
                )).from(trackPlayCount)
                .innerJoin(trackPlayCount.user, user).on(user.eqAny(
                        JPAExpressions.select(following.userTo)
                                .from(following)
                                .join(following.userFrom, fromUser)
                                .on(fromUser.email.eq(userEmail))))
                .innerJoin(trackPlayCount.track, track)
                .innerJoin(track.trackArtistList, trackArtist)
                .groupBy(trackPlayCount, track)
                .orderBy(aliasCount.desc())
                .limit(20)
                .fetch();
    }

    @Override
    public List<TrackPlayCountResponse> findTrackPlayCountByMostListenedTrack(String userEmail) {
        NumberPath<Long> aliasCount = Expressions.numberPath(Long.class, "count");

        return queryFactory.select(Projections.constructor(
                        TrackPlayCountResponse.class,
                        trackPlayCount.count().as(aliasCount),
                        Projections.constructor(TrackArtistResponse.class,
                                Projections.constructor(TrackResponse.class,
                                        track.id,
                                        track.trackTitle,
                                        track.durationTimeMs,
                                        track.recommendCount,
                                        track.albumImageUrl,
                                        track.spotifyTrackId,
                                        track.spotifyTrackHref
                                ),
                                Projections.constructor(ArtistResponse.class,
                                        artist.spotifyArtistId.min(),
                                        artist.artistName.min(),
                                        artist.artistImageUrl.min()
                                )
                        )
                )).from(trackPlayCount)
                .innerJoin(trackPlayCount.user, user).on(user.email.eq(userEmail))
                .innerJoin(trackPlayCount.track, track)
                .innerJoin(track.trackArtistList, trackArtist)
                .groupBy(track)
                .orderBy(aliasCount.desc())
                .limit(20)
                .fetch();
    }
}
