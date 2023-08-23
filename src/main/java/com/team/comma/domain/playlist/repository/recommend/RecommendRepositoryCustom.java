package com.team.comma.domain.playlist.repository.recommend;

import com.team.comma.domain.playlist.domain.Recommend;
import com.team.comma.domain.playlist.dto.recommend.RecommendResponse;
import com.team.comma.domain.user.domain.User;

import java.util.List;

public interface RecommendRepositoryCustom {

    List<RecommendResponse> getRecommendsByToUser(User user);

    List<RecommendResponse> getRecommendsByFromUser(User user);

    List<RecommendResponse> getRecommendsToAnonymous();

    long getRecommendCountByToUserAndPlaylist(Recommend reco);

    long increasePlayCount(long recommendId);
}
