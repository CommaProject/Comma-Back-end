package com.team.comma.domain.favorite.repository;

import com.team.comma.domain.track.domain.Track;

import java.util.List;

public interface FavoriteTrackRepositoryCustom {

    List<Track> findFavoriteTrackByEmail(String userEmail);

}
