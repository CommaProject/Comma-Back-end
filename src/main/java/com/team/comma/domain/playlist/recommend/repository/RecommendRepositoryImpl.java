package com.team.comma.domain.playlist.recommend.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.playlist.recommend.domain.Recommend;
import com.team.comma.domain.playlist.recommend.dto.RecommendResponse;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.user.user.domain.QUser.user;
import static com.team.comma.domain.playlist.playlist.domain.QPlaylist.playlist;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.team.comma.domain.playlist.recommend.constant.RecommendType.ANONYMOUS;
import static com.team.comma.domain.playlist.recommend.domain.QRecommend.recommend;
import static com.team.comma.domain.playlist.track.domain.QPlaylistTrack.playlistTrack;

@RequiredArgsConstructor
public class RecommendRepositoryImpl implements RecommendRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecommendResponse> getRecommendsByToUser(User requestUser) {

        return queryFactory.select(
                        Projections.constructor(
                                RecommendResponse.class,
                                recommend.id,
                                recommend.comment,
                                user.userDetail.nickname,
                                user.userDetail.profileImageUrl,
                                recommend.playlist.id,
                                recommend.playlist.playlistTitle,
                                select(playlistTrack.track.albumImageUrl)
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist))
                                        .orderBy(playlistTrack.playSequence.asc())
                                        .limit(1),
                                select(playlistTrack.count())
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist)),
                                recommend.playCount
                        ))
                .from(recommend)
                .innerJoin(recommend.playlist , playlist)
                .innerJoin(playlist.user , user)
                .where(recommend.toUser.eq(requestUser))
                .orderBy(recommend.id.desc())
                .fetch();
    }

    @Override
    public List<RecommendResponse> getRecommendsByFromUser(User requestUser) {
        return queryFactory.select(
                        Projections.constructor(
                                RecommendResponse.class,
                                recommend.id,
                                recommend.comment,
                                recommend.toUser.userDetail.nickname,
                                recommend.toUser.userDetail.profileImageUrl,
                                recommend.playlist.id,
                                recommend.playlist.playlistTitle,
                                select(playlistTrack.track.albumImageUrl)
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist))
                                        .orderBy(playlistTrack.playSequence.asc())
                                        .limit(1),
                                select(playlistTrack.count())
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist)),
                                recommend.playCount
                        ))
                .from(recommend)
                .innerJoin(recommend.playlist , playlist)
                .innerJoin(playlist.user , user).on(user.eq(requestUser))
                .orderBy(recommend.id.desc())
                .fetch();
    }

    @Override
    public List<RecommendResponse> getRecommendsToAnonymous() {
        return queryFactory.select(
                        Projections.constructor(
                                RecommendResponse.class,
                                recommend.id,
                                recommend.comment,
                                user.userDetail.nickname,
                                user.userDetail.profileImageUrl,
                                recommend.playlist.id,
                                recommend.playlist.playlistTitle,
                                select(playlistTrack.track.albumImageUrl)
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist))
                                        .orderBy(playlistTrack.playSequence.asc())
                                        .limit(1),
                                select(playlistTrack.count())
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist)),
                                recommend.playCount
                        ))
                .from(recommend)
                .innerJoin(recommend.playlist , playlist)
                .innerJoin(playlist.user , user)
                .where(recommend.recommendType.eq(ANONYMOUS))
                .orderBy(recommend.id.desc())
                .fetch();
    }

    @Override
    public long getRecommendCountByToUserAndPlaylist(Recommend reco) {
        return queryFactory
                .select(recommend.count())
                .from(recommend)
                .where(recommend.toUser.eq(reco.getToUser())
                        .and(recommend.playlist.eq(reco.getPlaylist())))
                .fetchFirst();
    }

    @Override
    public long increasePlayCount(long recommendId) {
        return queryFactory.update(recommend)
                .set(recommend.playCount, recommend.playCount.add(1L))
                .where(recommend.id.eq(recommendId))
                .execute();
    }
}
