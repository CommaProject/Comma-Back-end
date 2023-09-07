package com.team.comma.domain.artist.repository;

import com.team.comma.domain.artist.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
