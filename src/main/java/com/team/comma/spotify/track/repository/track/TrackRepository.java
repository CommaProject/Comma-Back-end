package com.team.comma.spotify.track.repository.track;

import com.team.comma.spotify.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> , CustomTrackRepository {

    Optional<Track> findBySpotifyTrackId(String spotifyTrackId);

}
