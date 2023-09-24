package com.team.comma.domain.favorite.artist.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.team.comma.domain.artist.dto.ArtistResponse;
import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.favorite.artist.dto.FavoriteArtistResponse;
import com.team.comma.domain.user.user.domain.QUser;
import com.team.comma.domain.user.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.team.comma.domain.favorite.artist.domain.QFavoriteArtist.favoriteArtist;

@RequiredArgsConstructor
public class FavoriteArtistRepositoryImpl implements FavoriteArtistRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QUser qUser = new QUser("user");

    @Override
    public List<String> findFavoriteArtistListByUser(User user) {
        return queryFactory.select(favoriteArtist.artist.artistName)
                .from(favoriteArtist)
                .where(favoriteArtist.user.eq(user))
                .fetch();
    }

    @Override
    public Optional<FavoriteArtist> findFavoriteArtistByUser(User user, String artistName) {
        FavoriteArtist result = queryFactory.select(favoriteArtist).from(favoriteArtist)
                .innerJoin(favoriteArtist.user , qUser).on(qUser.eq(user))
                .where(favoriteArtist.artist.artistName.eq(artistName))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<FavoriteArtistResponse> findAllFavoriteArtistByUser(User user) {
        return queryFactory.select(
                Projections.constructor(
                        FavoriteArtistResponse.class,
                        favoriteArtist.id,
                        Projections.constructor(
                                ArtistResponse.class,
                                favoriteArtist.artist.spotifyArtistId,
                                favoriteArtist.artist.artistName
                        )))
                .from(favoriteArtist)
                .where(favoriteArtist.user.eq(user))
                .fetch();
    }
}
