package com.team.comma.domain.playlist.track.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.dto.PlaylistTrackResponse;
import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.dto.TrackResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.artist.domain.QArtist.artist;
import static com.team.comma.domain.playlist.track.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.domain.track.track.domain.QTrack.track;
import static com.team.comma.domain.track.artist.domain.QTrackArtist.trackArtist;

@RequiredArgsConstructor
public class PlaylistTrackRepositoryImpl implements PlaylistTrackRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist) {

        return queryFactory.select(
                Projections.constructor(
                        PlaylistTrackResponse.class,
                        playlistTrack.trackAlarmFlag,
                        Projections.list(Projections.constructor(
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
                                        artist.artistName.min()
                                )))))
                .from(playlistTrack)
                .innerJoin(playlistTrack.track , track)
                .innerJoin(track.trackArtistList , trackArtist)
                .innerJoin(trackArtist.artist , artist)
                .where(playlistTrack.playlist.eq(playlist))
                .groupBy(playlistTrack , track)
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
