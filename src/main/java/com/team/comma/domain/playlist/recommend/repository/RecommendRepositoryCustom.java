package com.team.comma.domain.playlist.recommend.repository;

import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.user.user.domain.User;

import java.util.List;

public interface RecommendRepositoryCustom {

    List<RecommendResponse> findAllRecommendsByToUser(User user);

    List<RecommendResponse> findAllRecommendsByFromUser(User user);

    List<RecommendResponse> findAllRecommendsByToAnonymous();

    long increasePlayCount(long recommendId);


}
