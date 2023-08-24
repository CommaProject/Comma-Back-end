package com.team.comma.domain.favorite.track.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.track.track.domain.Track;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.favorite.track.domain.QFavoriteTrack.favoriteTrack;
import static com.team.comma.domain.user.user.domain.QUser.user;

@RequiredArgsConstructor
public class FavoriteTrackRepositoryImpl implements FavoriteTrackRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Track> findFavoriteTrackByEmail(String userEmail) {
        return jpaQueryFactory.select(favoriteTrack.track).from(favoriteTrack)
                .join(favoriteTrack.user , user).on(user.email.eq(userEmail)).fetch();
    }
}
