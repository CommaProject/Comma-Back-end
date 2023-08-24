package com.team.comma.domain.favorite.artist.repository;

import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.favorite.domain.FavoriteArtist;
import com.team.comma.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Long> , FavoriteArtistRepositoryCustom {
    List<FavoriteArtist> findAllByUser(User user);
}
