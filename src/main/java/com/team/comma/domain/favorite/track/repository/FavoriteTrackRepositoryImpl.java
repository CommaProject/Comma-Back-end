package com.team.comma.domain.favorite.track.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.favorite.track.domain.QFavoriteTrack.favoriteTrack;
import static com.team.comma.domain.track.artist.domain.QTrackArtist.trackArtist;
import static com.team.comma.domain.track.track.domain.QTrack.track;
import static com.team.comma.domain.user.user.domain.QUser.user;

@RequiredArgsConstructor
public class FavoriteTrackRepositoryImpl implements FavoriteTrackRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Track> findFavoriteTrackByEmail(String userEmail) {
        return jpaQueryFactory.select(favoriteTrack.track).from(favoriteTrack)
                .join(favoriteTrack.user , user).on(user.email.eq(userEmail)).fetch();
    }

    @Override
    public List<FavoriteTrackResponse> findAllFavoriteTrackByUser(User user) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        FavoriteTrackResponse.class,
                        favoriteTrack.id,
                        track.id,
                        track.trackTitle,
                        track.albumImageUrl,
                        track.spotifyTrackId,
                        Projections.list(Projections.constructor(
                                TrackArtistResponse.class,
                                trackArtist.id,
                                trackArtist.artist))
                ))
                .from(favoriteTrack)
                .join(track)
                .join(trackArtist)
                .where(favoriteTrack.user.eq(user))
                .orderBy(favoriteTrack.id.desc())
                .fetch();
    }
}
