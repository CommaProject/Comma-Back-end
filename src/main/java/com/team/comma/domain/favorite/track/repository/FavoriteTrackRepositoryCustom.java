package com.team.comma.domain.favorite.track.repository;

import com.team.comma.domain.favorite.track.dto.FavoriteTrackResponse;
import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;

import java.util.List;

public interface FavoriteTrackRepositoryCustom {

    List<Track> findFavoriteTrackByEmail(String userEmail);

    List<FavoriteTrackResponse> findAllFavoriteTrackByUser(User user);
}
