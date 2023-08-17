package com.team.comma.domain.spotify.playlist.repository;

import com.team.comma.domain.spotify.playlist.domain.Playlist;
import com.team.comma.domain.spotify.playlist.dto.PlaylistTrackResponse;

import java.util.List;

public interface PlaylistTrackRepositoryCustom {

    List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist);
}
