package com.team.comma.domain.playlist.playlist.repository;

import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.user.user.domain.User;

import java.util.List;

public interface PlaylistRepositoryCustom {

    int getTotalDurationTimeMsWithPlaylistId(Long playlistId);

    int findMaxListSequence();

    long updateAlarmFlag(long id, boolean alarmFlag);

    long deletePlaylist(long id);

    List<PlaylistResponse> getPlaylistsByUser(User user);
}
