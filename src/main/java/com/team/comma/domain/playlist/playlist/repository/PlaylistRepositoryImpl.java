package com.team.comma.domain.playlist.playlist.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.alert.dto.AlertResponse;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.dto.PlaylistResponse;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.team.comma.domain.user.detail.domain.QUserDetail.userDetail;
import static com.team.comma.domain.playlist.playlist.domain.QPlaylist.playlist;
import static com.team.comma.domain.playlist.track.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.domain.track.track.domain.QTrack.track;
import static com.team.comma.domain.user.user.domain.QUser.user;

@RequiredArgsConstructor
public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaylistResponse> findAllPlaylistsByUser(User user) {
        return queryFactory.select(
                        Projections.constructor(
                                PlaylistResponse.class,
                                playlist.id,
                                playlist.playlistTitle,
                                playlist.alarmFlag,
                                playlist.alarmStartTime,
                                playlist.playlistTrackList.size(),
                                track.albumImageUrl.max(),
                                track.durationTimeMs.sum().coalesce(0).longValue()
                        ))
                .from(playlist)
                .join(playlist.playlistTrackList, playlistTrack)
                .join(playlistTrack.track, track)
                .where(playlist.delFlag.eq(false)
                        .and(playlist.user.eq(user)))
                .groupBy(playlist)
                .orderBy(playlist.alarmStartTime.asc().nullsLast())
                .fetch();
    }

    @Override
    public Optional<PlaylistResponse> findPlaylistByPlaylistId(long playlistId) {
        PlaylistResponse result = queryFactory.select(
                        Projections.constructor(
                                PlaylistResponse.class,
                                playlist.id,
                                playlist.playlistTitle,
                                playlist.alarmFlag,
                                playlist.alarmStartTime,
                                playlist.playlistTrackList.size(),
                                track.albumImageUrl.max(),
                                track.durationTimeMs.sum().coalesce(0).longValue()
                        ))
                .from(playlist)
                .join(playlist.playlistTrackList, playlistTrack)
                .join(playlistTrack.track, track)
                .where(playlist.delFlag.eq(false)
                        .and(playlist.id.eq(playlistId)))
                .groupBy(playlist)
                .orderBy(playlist.alarmStartTime.asc())
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public long updateRecommendCountByPlaylistId(long playlistId) {
        return queryFactory.update(track)
                .set(track.recommendCount, track.recommendCount.add(1))
                .where(track.id.in(
                        JPAExpressions.select(track.id)
                                .from(playlistTrack)
                                .innerJoin(playlistTrack.playlist, playlist)
                                .on(playlist.id.eq(playlistId))
                                .innerJoin(playlistTrack.track, track)
                )).execute();
    }

    @Override
    public List<AlertResponse> findAllPlaylistsByAlertTime(LocalTime time) {
        LocalTime start = LocalTime.of(time.getHour(), time.getMinute() - 5).withNano(0);
        LocalTime end = LocalTime.of(time.getHour(), time.getMinute() + 5).withNano(0);

        return queryFactory.select(Projections.constructor(
                        AlertResponse.class,
                        user.id,
                        playlist.id,
                        playlist.playlistTitle,
                        playlist.alarmFlag,
                        playlist.alarmStartTime,
                        playlist.playlistTrackList.size(),
                        track.albumImageUrl.max(),
                        track.durationTimeMs.sum().coalesce(0).longValue()
                ))
                .from(playlist)
                .innerJoin(playlist.user, user)
                .innerJoin(user.userDetail, userDetail).on(userDetail.popupAlertFlag.eq(true))
                .innerJoin(playlist.playlistTrackList, playlistTrack)
                .innerJoin(playlistTrack.track, track)
                .where(playlist.alarmStartTime.goe(start).and(playlist.alarmStartTime.loe(end)))
                .groupBy(playlist)
                .fetch();
    }

    @Override
    public int findTotalDurationTimeMsByPlaylistId(Long playlistId) {
        return queryFactory.select(track.durationTimeMs.sum().coalesce(0))
                .from(playlist)
                .innerJoin(track).fetchJoin()
                .where(playlist.id.eq(playlistId))
                .fetchOne();

    }

    @Override
    public long deletePlaylists(List<Long> playlistIdList) {
        return queryFactory.update(playlist)
                .set(playlist.delFlag, true)
                .where(playlist.id.in(playlistIdList))
                .execute();
    }


}
