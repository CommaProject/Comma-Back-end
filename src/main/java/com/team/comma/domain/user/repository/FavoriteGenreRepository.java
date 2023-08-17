package com.team.comma.domain.user.repository;

import com.team.comma.domain.user.domain.FavoriteGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Long> , FavoriteGenreRepositoryCustom {

}
