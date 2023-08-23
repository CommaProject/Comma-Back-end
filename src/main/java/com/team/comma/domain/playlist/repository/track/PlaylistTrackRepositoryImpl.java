package com.team.comma.domain.playlist.repository.track;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.playlist.dto.track.PlaylistTrackArtistResponse;
import com.team.comma.domain.playlist.dto.track.PlaylistTrackResponse;
import com.team.comma.domain.playlist.domain.Playlist;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.playlist.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.domain.track.domain.QTrackArtist.trackArtist;

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
