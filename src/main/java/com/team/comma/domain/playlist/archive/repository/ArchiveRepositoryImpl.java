package com.team.comma.domain.playlist.archive.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.team.comma.domain.playlist.archive.domain.QArchive.archive;

@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepositoryCustom  {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ArchiveResponse> findAllArchiveByDate(User user, LocalDateTime startDate) {
        return queryFactory.select(
                Projections.constructor(
                        ArchiveResponse.class,
                        archive.id,
                        archive.comment,
                        archive.createdAt,
                        archive.playlist.id,
                        archive.playlist.playlistTitle,
                        archive.playlist.playlistTrackList.get(0).track.albumImageUrl
                )
        )
                .from(archive)
                .where(archive.createdAt.after(startDate))
                .orderBy(archive.id.asc())
                .fetch();
    }
}
