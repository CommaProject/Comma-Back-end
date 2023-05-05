package com.team.comma.spotify.playlist.repository;

import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {

    List<PlaylistTrack> findAllByPlaylist_Id(Long playlistId);

    int deletePlaylistTrackByTrackIdAndPlaylistId(Long trackId, Long playlistId);


    Optional<PlaylistTrack> findByTrackIdAndPlaylistId(Long trackId, Long playlistId);

}
