package com.team.comma.domain.playlist.archive.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.team.comma.domain.playlist.archive.domain.QArchive.archive;
import static com.team.comma.domain.playlist.playlist.domain.QPlaylist.playlist;
import static com.team.comma.domain.track.track.domain.QTrack.track;

@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepositoryCustom  {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ArchiveResponse> findArchiveByDateTime(User user, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return queryFactory.select(
                Projections.constructor(
                        ArchiveResponse.class,
                        archive.id,
                        archive.comment,
                        archive.createDate,
                        archive.publicFlag,
                        playlist.id,
                        playlist.playlistTitle,
                        track.albumImageUrl
                ))
                .from(archive)
                .join(playlist)
                .join(track)
                .where(archive.user.eq(user)
                        .and(archive.createDate.after(startDateTime))
                        .and(archive.createDate.before(endDateTime))
                )
                .groupBy(archive, playlist, track)
                .orderBy(archive.id.asc())
                .fetch();
    }
}
