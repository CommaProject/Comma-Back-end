package com.team.comma.spotify.playlist.repository;

import static com.querydsl.core.types.Projections.list;
import static com.team.comma.spotify.playlist.domain.QPlaylist.playlist;
import static com.team.comma.spotify.playlist.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.spotify.track.domain.QTrack.track;
import static com.team.comma.spotify.track.domain.QTrackArtist.trackArtist;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {


    private final JPAQueryFactory queryFactory;


    @Override
    public int getTotalDurationTimeMsWithPlaylistId(Long playlistId) {
        return queryFactory.select(track.durationTimeMs.sum().coalesce(0))
            .from(playlist)
            .innerJoin(track).fetchJoin()
            .where(playlist.id.eq(playlistId))
            .fetchOne();

    }

    @Override
    public int findMaxListSequence() {
        return queryFactory.select(playlist.listSequence.max().coalesce(0))
            .from(playlist)
            .fetchOne();
    }

    @Override
    public long updateAlarmFlag(long id, boolean alarmFlag) {
        return queryFactory.update(playlist)
                .set(playlist.alarmFlag, alarmFlag)
                .where(playlist.id.eq(id))
                .execute();
    }

    @Override
    public long deletePlaylist(long id) {
        return queryFactory.update(playlist)
                .set(playlist.delFlag,true)
                .where(playlist.id.eq(id))
                .execute();
    }

    @Override
    public List<PlaylistResponse> getPlaylistByUser(User user) {
        return queryFactory
                .select(
                    Projections.constructor(
                    PlaylistResponse.class,
                    playlist.id,
                    playlist.playlistTitle,
                    playlist.alarmFlag,
                    playlist.alarmStartTime,
                    list(Projections.constructor(
                        PlaylistTrackResponse.class,
                        track.id,
                        track.trackTitle,
                        track.durationTimeMs,
                        track.albumImageUrl,
                        playlistTrack.trackAlarmFlag,
                        list(Projections.constructor(
                            PlaylistTrackArtistResponse.class,
                            trackArtist.id,
                            trackArtist.artistName))))))
                .from(playlist)
                .leftJoin(playlist.playlistTrackList, playlistTrack)
                .leftJoin(playlistTrack.track, track)
                .leftJoin(track.trackArtistList, trackArtist)
                .where(playlist.delFlag.eq(false)
                        .and(playlist.user.eq(user)))
                .orderBy(playlist.alarmStartTime.asc())
                .fetch();
    }


}
