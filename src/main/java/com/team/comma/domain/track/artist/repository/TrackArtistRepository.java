package com.team.comma.domain.track.artist.repository;

import com.team.comma.domain.track.artist.domain.TrackArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackArtistRepository extends JpaRepository<TrackArtist, Long> {
}
