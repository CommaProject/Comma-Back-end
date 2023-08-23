package com.team.comma.domain.playlist.recommend.repository;

import com.team.comma.domain.playlist.recommend.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long>,
        RecommendRepositoryCustom {

}
