package com.team.comma.spotify.playlist.repository;

import com.team.comma.spotify.playlist.domain.Playlist;

public interface PlaylistTrackRepositoryCustom {

    Object getPlaylistTracksByPlaylist(Playlist playlist);
}
