package com.team.comma.domain.favorite.track.repository;

import com.team.comma.domain.favorite.track.domain.FavoriteTrack;
import com.team.comma.domain.user.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteTrackRepository extends JpaRepository<FavoriteTrack, Long> , FavoriteTrackRepositoryCustom {

}
