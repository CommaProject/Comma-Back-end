package com.team.comma.domain.spotify.track.repository.favorite;

import com.team.comma.domain.spotify.track.domain.FavoriteTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTrackRepository extends JpaRepository<FavoriteTrack, Long> , CustomFavoriteTrackRepository {
}
