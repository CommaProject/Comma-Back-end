package com.team.comma.domain.playlist.recommend.repository;

import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.user.user.domain.User;

import java.util.List;

public interface RecommendRepositoryCustom {

    List<RecommendResponse> getRecommendsByToUser(User user);

    List<RecommendResponse> getRecommendsByFromUser(User user);

    List<RecommendResponse> getRecommendsToAnonymous();

    long getRecommendCountByToUserAndPlaylist(Recommend reco);

    long increasePlayCount(long recommendId);
}
