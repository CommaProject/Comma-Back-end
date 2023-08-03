package com.team.comma.spotify.track.repository.favorite;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.spotify.track.domain.FavoriteTrack;
import com.team.comma.spotify.track.domain.Track;
import lombok.RequiredArgsConstructor;

import java.util.List;
import static com.team.comma.user.domain.QUser.user;
import static com.team.comma.spotify.track.domain.QFavoriteTrack.favoriteTrack;

@RequiredArgsConstructor
public class FavoriteTrackRepositoryImpl implements CustomFavoriteTrackRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Track> findFavoriteTrackByEmail(String userEmail) {
        return jpaQueryFactory.select(favoriteTrack.track).from(favoriteTrack)
                .join(favoriteTrack.user , user).on(user.email.eq(userEmail)).fetch();
    }
}
