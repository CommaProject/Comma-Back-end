package com.team.comma.domain.alertday.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import lombok.RequiredArgsConstructor;

import static com.team.comma.domain.playlist.alertDay.domain.QAlertDay.alertDay;

@RequiredArgsConstructor
public class AlertDayRepositoryImpl implements AlertDayRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteAllAlertDaysByPlaylist(Playlist playlist) {
        return queryFactory.delete(alertDay)
                .where(alertDay.playlist.eq(playlist))
                .execute();
    }
}
