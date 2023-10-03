package com.team.comma.domain.playlist.recommend.repository;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.user.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long>, RecommendRepositoryCustom {

    Optional<Recommend> findByPlaylistAndToUser(Playlist playlist, User user);

}
