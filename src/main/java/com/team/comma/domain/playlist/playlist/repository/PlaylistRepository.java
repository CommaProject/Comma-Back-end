package com.team.comma.domain.playlist.playlist.repository;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>,
    PlaylistRepositoryCustom {

}
