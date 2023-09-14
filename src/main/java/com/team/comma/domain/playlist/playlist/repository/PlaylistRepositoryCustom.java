package com.team.comma.domain.playlist.playlist.repository;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.user.user.domain.User;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepositoryCustom {

    int findTotalDurationTimeMsByPlaylistId(Long playlistId);

    long deletePlaylists(List<Long> playlistIdList);

    List<PlaylistResponse> findAllPlaylistsByUser(User user);

    Optional<PlaylistResponse> findPlaylistByPlaylistId(long playlistId);

    long updateRecommendCountByPlaylistId(long playlistId);

    List<Playlist> findAllPlaylistsByAlertTime(LocalTime time);

}
