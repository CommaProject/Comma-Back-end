package com.team.comma.domain.track.repository;

import com.team.comma.domain.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> , TrackRepositoryCustom {

    Optional<Track> findBySpotifyTrackId(String spotifyTrackId);

}
