package com.team.comma.domain.favorite.track.repository;

import com.team.comma.domain.track.track.domain.Track;

import java.util.List;

public interface FavoriteTrackRepositoryCustom {

    List<Track> findFavoriteTrackByEmail(String userEmail);

}
