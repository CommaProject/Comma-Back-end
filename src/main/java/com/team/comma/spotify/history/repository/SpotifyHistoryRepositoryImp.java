package com.team.comma.spotify.history.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.spotify.history.dto.HistoryResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.spotify.history.domain.QHistory.history;
import static com.team.comma.user.domain.QUser.user;

@RequiredArgsConstructor
public class SpotifyHistoryRepositoryImp implements SpotifyHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HistoryResponse> getHistoryListByUserEmail(String userEmail) {
        return queryFactory.select(Projections.constructor(HistoryResponse.class, history.id, history.searchHistory))
                .from(history)
                .innerJoin(history.user, user)
                .where(history.user.email.eq(userEmail).and(history.delFlag.eq(true)))
                .fetch();
    }

    @Override
    public void deleteHistoryById(long id) {
        queryFactory.update(history)
                .set(history.delFlag, true)
                .where(history.id.eq(id))
                .execute();
    }
}

