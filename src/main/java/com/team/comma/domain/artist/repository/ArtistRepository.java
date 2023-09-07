package com.team.comma.domain.artist.repository;

import com.team.comma.domain.artist.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findArtistByArtistId(String artistId);

}
