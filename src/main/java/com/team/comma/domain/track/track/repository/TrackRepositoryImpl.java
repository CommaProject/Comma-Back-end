package com.team.comma.domain.track.track.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.track.track.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.dto.TrackResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.artist.domain.QArtist.artist;
import static com.team.comma.domain.track.artist.domain.QTrackArtist.trackArtist;
import static com.team.comma.domain.track.track.domain.QTrack.track;

@RequiredArgsConstructor
public class TrackRepositoryImpl implements TrackRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void updateTrackRecommendCount(String trackId) {
        jpaQueryFactory.update(track)
                .set(track.recommendCount, track.recommendCount.add(1))
                .where(track.spotifyTrackId.eq(trackId))
                .execute();
    }

    @Override
    public List<TrackArtistResponse> findTrackMostRecommended() {
        return jpaQueryFactory.select(Projections.constructor(
                        TrackArtistResponse.class,
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
                                artist.spotifyArtistName.min()
                        )
                )).from(track)
                .innerJoin(track.trackArtistList, trackArtist)
                .innerJoin(trackArtist.artist, artist)
                .groupBy(track)
                .orderBy(track.recommendCount.desc())
                .limit(20)
                .fetch();
    }

}
