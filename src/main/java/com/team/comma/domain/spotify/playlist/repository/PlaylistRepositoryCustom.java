package com.team.comma.domain.spotify.playlist.repository;

import com.team.comma.domain.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.domain.user.domain.User;

import java.util.List;

public interface PlaylistRepositoryCustom {

    int getTotalDurationTimeMsWithPlaylistId(Long playlistId);

    int findMaxListSequence();

    long updateAlarmFlag(long id, boolean alarmFlag);

    long deletePlaylist(long id);

    List<PlaylistResponse> getPlaylistsByUser(User user);
}
