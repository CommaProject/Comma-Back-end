package com.team.comma.repository;

import com.team.comma.domain.Playlist;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByUser_Email(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE playlist_tb p set p.del_flag = 'Y' where p.id = :id")
    void deletePlaylist(@Param("id") Long id);
}
