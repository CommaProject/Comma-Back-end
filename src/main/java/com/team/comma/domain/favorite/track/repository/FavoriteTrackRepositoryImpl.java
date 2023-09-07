package com.team.comma.domain.favorite.track.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.artist.domain.QArtist.artist;
import static com.team.comma.domain.favorite.track.domain.QFavoriteTrack.favoriteTrack;
import static com.team.comma.domain.track.artist.domain.QTrackArtist.trackArtist;
import static com.team.comma.domain.track.track.domain.QTrack.track;
import static com.team.comma.domain.user.user.domain.QUser.user;

@RequiredArgsConstructor
public class FavoriteTrackRepositoryImpl implements FavoriteTrackRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<TrackArtistResponse> findFavoriteTrackByEmail(String userEmail) {
        return jpaQueryFactory.select(Projections.constructor(TrackArtistResponse.class,
                        favoriteTrack.track,
                        Projections.list(artist)
                )).from(favoriteTrack)
                .join(favoriteTrack.user, user).on(user.email.eq(userEmail))
                .innerJoin(favoriteTrack.track, track)
                .innerJoin(track.trackArtistList, trackArtist)
                .innerJoin(trackArtist.artist , artist)
                .fetch();
    }

    @Override
    public List<FavoriteTrackResponse> findAllFavoriteTrackByUser(User user) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                FavoriteTrackResponse.class,
                                favoriteTrack.id,
                                Projections.list(Projections.constructor(
                                        TrackArtistResponse.class,
                                        track ,
                                        Projections.list(artist)))
                        ))
                .from(favoriteTrack)
                .innerJoin(favoriteTrack.track, track)
                .innerJoin(track.trackArtistList, trackArtist)
                .innerJoin(trackArtist.artist , artist)
                .where(favoriteTrack.user.eq(user))
                .orderBy(favoriteTrack.id.desc())
                .fetch();
    }
}
