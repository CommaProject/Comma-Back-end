package com.team.comma.domain.track.track.repository.favorite;

import com.team.comma.domain.track.favoriteTrack.FavoriteTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTrackRepository extends JpaRepository<FavoriteTrack, Long> , CustomFavoriteTrackRepository {
}
