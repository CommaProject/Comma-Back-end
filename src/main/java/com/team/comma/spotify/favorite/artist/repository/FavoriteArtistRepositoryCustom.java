package com.team.comma.spotify.favorite.artist.repository;

import com.team.comma.user.domain.User;

import java.util.List;

public interface FavoriteArtistRepositoryCustom {
    List<String> findArtistListByUser(User user);
    void deleteByUser(User user , String artistName);
}
