package com.team.comma.domain.user.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.domain.user.favoriteGenre.QFavoriteGenre.favoriteGenre;

@RequiredArgsConstructor
public class FavoriteGenreRepositoryImpl implements FavoriteGenreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findByGenreNameList(User user) {
        return queryFactory.select(favoriteGenre.genreName)
                .from(favoriteGenre)
                .where(favoriteGenre.user.eq(user))
                .fetch();
    }
}
