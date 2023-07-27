package com.team.comma.spotify.recommend.repository;

import com.team.comma.spotify.recommend.domain.Recommend;
import com.team.comma.spotify.recommend.dto.RecommendResponse;
import com.team.comma.user.domain.User;

import java.util.List;

public interface RecommendRepositoryCustom {

    List<RecommendResponse> getRecommendsByToUser(User user);

    long getRecommendCountByToUserAndPlaylist(Recommend reco);
}
