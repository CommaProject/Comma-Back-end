package com.team.comma.domain.track.track.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.track.artist.domain.TrackArtist;
import com.team.comma.domain.track.track.domain.Track;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.team.comma.domain.track.artist.domain.QTrackArtist.trackArtist;
import static com.team.comma.domain.track.track.domain.QTrack.track;

@RequiredArgsConstructor
public class TrackRepositoryImpl implements TrackRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void updateTrackRecommendCount(String trackId) {
        jpaQueryFactory.update(track)
                .set(track.recommendCount , track.recommendCount.add(1))
                .where(track.spotifyTrackId.eq(trackId))
                .execute();
    }

    @Override
    public List<Track> findTrackMostRecommended() {
        return jpaQueryFactory.select(track).from(track)
                .orderBy(track.recommendCount.desc())
                .limit(20)
                .fetch();
    }

    @Override
    public Optional<Artist> findArtistByTrackId(long trackId) {
        Artist result = jpaQueryFactory.select(trackArtist.artist).from(trackArtist)
                .innerJoin(trackArtist.track).on(track.id.eq(trackId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
