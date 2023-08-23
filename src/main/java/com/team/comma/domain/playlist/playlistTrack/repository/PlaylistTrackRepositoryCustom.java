package com.team.comma.domain.playlist.playlistTrack.repository;

import com.team.comma.domain.playlist.playlistTrack.dto.PlaylistTrackResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;

import java.util.List;

public interface PlaylistTrackRepositoryCustom {

    List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist);
}
