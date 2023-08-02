package com.team.comma.spotify.track.repository.favorite;

import com.team.comma.spotify.track.domain.FavoriteTrack;

import java.util.List;

public interface CustomFavoriteTrackRepository {

    List<FavoriteTrack> findFavoriteTrackByEmail(String userEmail);

}
