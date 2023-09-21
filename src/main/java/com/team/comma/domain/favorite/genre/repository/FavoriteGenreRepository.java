package com.team.comma.domain.favorite.genre.repository;

import com.team.comma.domain.favorite.genre.domain.FavoriteGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Long> , FavoriteGenreRepositoryCustom {

}
