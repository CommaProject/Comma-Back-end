package com.team.comma.domain.user.user.repository;

import com.team.comma.domain.user.favoriteGenre.FavoriteGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Long> , FavoriteGenreRepositoryCustom {

}
