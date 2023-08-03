package com.team.comma.spotify.track.repository.favorite;

import com.team.comma.spotify.track.domain.Track;

import java.util.List;

public interface CustomFavoriteTrackRepository {

    List<Track> findFavoriteTrackByEmail(String userEmail);

}
