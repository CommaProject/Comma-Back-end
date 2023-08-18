package com.team.comma.domain.spotify.recommend.repository;

import com.team.comma.domain.spotify.recommend.domain.Recommend;
import com.team.comma.domain.spotify.recommend.dto.RecommendResponse;
import com.team.comma.domain.user.domain.User;

import java.util.List;

public interface RecommendRepositoryCustom {

    List<RecommendResponse> getRecommendsByToUser(User user);

    List<RecommendResponse> getRecommendsByFromUser(User user);

    List<RecommendResponse> getRecommendsToAnonymous();

    long getRecommendCountByToUserAndPlaylist(Recommend reco);

    long increasePlayCount(long recommendId);
}
