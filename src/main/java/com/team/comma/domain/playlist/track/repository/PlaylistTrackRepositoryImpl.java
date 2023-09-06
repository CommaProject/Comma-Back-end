package com.team.comma.domain.playlist.track.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.playlist.track.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.domain.track.artist.domain.QTrackArtist.trackArtist;

@RequiredArgsConstructor
public class PlaylistTrackRepositoryImpl implements PlaylistTrackRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist) {
        return queryFactory.select(
                Projections.constructor(
                        PlaylistTrackResponse.class,
                        // playlisttrackId 추가 필요
                        playlistTrack.track.id,
                        playlistTrack.track.trackTitle,
                        playlistTrack.track.durationTimeMs,
                        playlistTrack.track.albumImageUrl,
                        playlistTrack.trackAlarmFlag,
                        // playlistsequence 추가 필요
                        Projections.list(Projections.constructor(
                                TrackArtistResponse.class,
                                trackArtist.id,
                                trackArtist.artist))))
                .from(playlistTrack)
                .leftJoin(trackArtist)
                .where(playlistTrack.playlist.eq(playlist))
                .orderBy(playlistTrack.playSequence.asc())
                .fetch();
    }

    @Override
    public long deletePlaylistTracksByIds(List<Long> playlistTrackIds) {
        return queryFactory.delete(playlistTrack)
                .where(playlistTrack.id.in(playlistTrackIds))
                .execute();
    }

}
