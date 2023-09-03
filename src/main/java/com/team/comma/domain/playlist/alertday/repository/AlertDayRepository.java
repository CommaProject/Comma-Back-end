package com.team.comma.domain.playlist.alertday.repository;

import com.team.comma.domain.playlist.alertday.domain.AlertDay;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertDayRepository extends JpaRepository<AlertDay, Long> {

    List<AlertDay> findAllByPlaylist(Playlist playlist);

}
