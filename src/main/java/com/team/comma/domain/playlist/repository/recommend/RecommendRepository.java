package com.team.comma.domain.playlist.repository.recommend;

import com.team.comma.domain.playlist.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long>,
        RecommendRepositoryCustom {

}
