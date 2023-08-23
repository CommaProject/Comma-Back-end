package com.team.comma.domain.favorite.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.track.domain.Track;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.favorite.domain.QFavoriteTrack.favoriteTrack;
import static com.team.comma.domain.user.domain.QUser.user;

@RequiredArgsConstructor
public class FavoriteTrackRepositoryImpl implements FavoriteTrackRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Track> findFavoriteTrackByEmail(String userEmail) {
        return jpaQueryFactory.select(favoriteTrack.track).from(favoriteTrack)
                .join(favoriteTrack.user , user).on(user.email.eq(userEmail)).fetch();
    }
}
