package com.team.comma.domain.playlist.repository.track;

import com.team.comma.domain.playlist.dto.track.PlaylistTrackResponse;
import com.team.comma.domain.playlist.domain.Playlist;

import java.util.List;

public interface PlaylistTrackRepositoryCustom {

    List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist);
}
