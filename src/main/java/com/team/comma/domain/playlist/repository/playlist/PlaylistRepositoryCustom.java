package com.team.comma.domain.playlist.repository.playlist;

import com.team.comma.domain.playlist.dto.playlist.PlaylistResponse;
import com.team.comma.domain.user.domain.User;

import java.util.List;

public interface PlaylistRepositoryCustom {

    int getTotalDurationTimeMsWithPlaylistId(Long playlistId);

    int findMaxListSequence();

    long updateAlarmFlag(long id, boolean alarmFlag);

    long deletePlaylist(long id);

    List<PlaylistResponse> getPlaylistsByUser(User user);
}
