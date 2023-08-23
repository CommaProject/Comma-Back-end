package com.team.comma.domain.track.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.track.domain.Track;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.track.domain.QTrack.track;

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
}
