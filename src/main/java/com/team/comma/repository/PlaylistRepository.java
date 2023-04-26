package com.team.comma.repository;

import com.team.comma.domain.Playlist;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByUser_Email(String email);

    @Modifying(clearAutomatically = true)
    @Query("update Playlist p set p.alarmFlag = :alarmFlag where p.id = :id")
    int updateAlarm(Long id, Boolean alarmFlag);
}
