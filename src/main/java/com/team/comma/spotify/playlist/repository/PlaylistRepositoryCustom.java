package com.team.comma.spotify.playlist.repository;

import org.springframework.data.repository.query.Param;

public interface PlaylistRepositoryCustom {

    int getTotalDurationTimeMsWithPlaylistId(@Param("playlistId") Long playlistId);

    int findMaxListSequence();

    long updateAlarmFlag(@Param("id") long id, @Param("alarmFlag") boolean alarmFlag);

    long deletePlaylist(@Param("id") long id);
}
