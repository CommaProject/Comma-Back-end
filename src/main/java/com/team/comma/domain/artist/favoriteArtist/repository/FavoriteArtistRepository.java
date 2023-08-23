package com.team.comma.domain.artist.favoriteArtist.repository;

import com.team.comma.domain.artist.favoriteArtist.domain.FavoriteArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Long> , FavoriteArtistRepositoryCustom {

}
