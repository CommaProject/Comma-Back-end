package com.team.comma.domain.playlist.track.repository;

import com.team.comma.domain.playlist.track.dto.PlaylistTrackResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;

import java.util.List;

public interface PlaylistTrackRepositoryCustom {

    List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist);
}
