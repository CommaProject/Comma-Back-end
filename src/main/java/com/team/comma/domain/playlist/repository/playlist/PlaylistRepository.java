package com.team.comma.domain.playlist.repository.playlist;

import com.team.comma.domain.playlist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>,
    PlaylistRepositoryCustom {

}
