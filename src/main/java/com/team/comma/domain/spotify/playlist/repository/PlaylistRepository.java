package com.team.comma.domain.spotify.playlist.repository;

import com.team.comma.domain.spotify.playlist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>,
    PlaylistRepositoryCustom {

}
