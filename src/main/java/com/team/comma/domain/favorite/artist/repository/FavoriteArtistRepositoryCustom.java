package com.team.comma.domain.favorite.artist.repository;

import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.user.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface FavoriteArtistRepositoryCustom {
    List<String> findFavoriteArtistListByUser(User user);
    void deleteByUser(User user , String artistName);

    Optional<FavoriteArtist> findFavoriteArtistByUser(User user , String artistName);

}
