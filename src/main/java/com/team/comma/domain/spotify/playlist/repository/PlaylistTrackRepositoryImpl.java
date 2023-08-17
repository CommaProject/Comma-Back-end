package com.team.comma.domain.spotify.playlist.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.spotify.playlist.domain.Playlist;
import com.team.comma.domain.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.domain.spotify.playlist.dto.PlaylistTrackResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.spotify.playlist.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.domain.spotify.track.domain.QTrackArtist.trackArtist;


@RequiredArgsConstructor
public class PlaylistTrackRepositoryImpl implements PlaylistTrackRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist) {
        return queryFactory.select(
                Projections.constructor(
                        PlaylistTrackResponse.class,
                        playlistTrack.track.id,
                        playlistTrack.track.trackTitle,
                        playlistTrack.track.durationTimeMs,
                        playlistTrack.track.albumImageUrl,
                        playlistTrack.trackAlarmFlag,
                        Projections.list(Projections.constructor(
                                PlaylistTrackArtistResponse.class,
                                trackArtist.id,
                                trackArtist.artistName))))
                .from(playlistTrack)
                .leftJoin(trackArtist)
                .where(playlistTrack.playlist.eq(playlist))
                .orderBy(playlistTrack.playSequence.asc())
                .fetch();
    }

}
