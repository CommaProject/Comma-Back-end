package com.team.comma.domain.spotify.favorite.artist.repository;

import com.team.comma.domain.spotify.favorite.artist.domain.FavoriteArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Long> , FavoriteArtistRepositoryCustom {

}