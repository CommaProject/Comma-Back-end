package com.team.comma.domain.playlist.playlist.repository;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.team.comma.domain.playlist.playlist.domain.QPlaylist.playlist;
import static com.team.comma.domain.playlist.track.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.domain.track.track.domain.QTrack.track;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public int findTotalDurationTimeMsByPlaylistId(Long playlistId) {
        return queryFactory.select(track.durationTimeMs.sum().coalesce(0))
            .from(playlist)
            .innerJoin(track).fetchJoin()
            .where(playlist.id.eq(playlistId))
            .fetchOne();

    }

    @Override
    public int findMaxListSequence() {
        return queryFactory.select(playlist.listSequence.max().coalesce(0))
            .from(playlist)
            .fetchOne();
    }

    @Override
    public long modifyAlarmFlag(long id, boolean alarmFlag) {
        return queryFactory.update(playlist)
                .set(playlist.alarmFlag, alarmFlag)
                .where(playlist.id.eq(id))
                .execute();
    }

    @Override
    public long deletePlaylist(long id) {
        return queryFactory.update(playlist)
                .set(playlist.delFlag,true)
                .where(playlist.id.eq(id))
                .execute();
    }

    @Override
    public List<PlaylistResponse> findAllPlaylistsByUser(User user) {
        return queryFactory.select(
                        Projections.constructor(
                                PlaylistResponse.class,
                                playlistTrack.playlist.id,
                                playlistTrack.playlist.playlistTitle,
                                playlistTrack.playlist.alarmFlag,
                                playlistTrack.playlist.alarmStartTime,
                                playlistTrack.track.albumImageUrl,
                                select(playlistTrack.count())
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(playlist))
                        ))
                .from(playlistTrack)
                .where(playlistTrack.playlist.delFlag.eq(false)
                        .and(playlistTrack.playlist.user.eq(user)))
                .orderBy(playlistTrack.playlist.alarmStartTime.asc())
                .fetch();
    }

    @Override
    public Optional<PlaylistResponse> findPlaylistsByPlaylistId(long playlistId) {
        PlaylistResponse result = queryFactory.select(
                        Projections.constructor(
                                PlaylistResponse.class,
                                playlistTrack.playlist.id,
                                playlistTrack.playlist.playlistTitle,
                                playlistTrack.playlist.alarmFlag,
                                playlistTrack.playlist.alarmStartTime,
                                playlistTrack.track.albumImageUrl,
                                select(playlistTrack.count())
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(playlist))
                        ))
                .from(playlistTrack)
                .where(playlistTrack.playlist.delFlag.eq(false)
                        .and(playlistTrack.playlist.id.eq(playlistId)))
                .orderBy(playlistTrack.playlist.alarmStartTime.asc())
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
