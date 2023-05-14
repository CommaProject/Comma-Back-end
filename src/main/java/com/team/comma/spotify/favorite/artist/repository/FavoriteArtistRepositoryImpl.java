package com.team.comma.spotify.favorite.artist.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.user.domain.QUser;
import com.team.comma.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.spotify.favorite.artist.domain.QFavoriteArtist.favoriteArtist;


@RequiredArgsConstructor
public class FavoriteArtistRepositoryImpl implements FavoriteArtistRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findArtistListByUser(User user) {
        return queryFactory.select(favoriteArtist.artistName)
                .from(favoriteArtist)
                .where(favoriteArtist.user.eq(user))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteByUser(User user , String artistName) {
        QUser qUser = new QUser("user");
        queryFactory.delete(favoriteArtist)
                .where(favoriteArtist.id.eq(
                        JPAExpressions.select(favoriteArtist.id).from(favoriteArtist)
                                .innerJoin(favoriteArtist.user , qUser).on(qUser.eq(user))
                                .where(favoriteArtist.artistName.eq(artistName))
                ))
                .execute();
    }
}
