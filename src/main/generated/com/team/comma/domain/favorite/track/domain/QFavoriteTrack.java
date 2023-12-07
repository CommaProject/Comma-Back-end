package com.team.comma.domain.favorite.track.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFavoriteTrack is a Querydsl query type for FavoriteTrack
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFavoriteTrack extends EntityPathBase<FavoriteTrack> {

    private static final long serialVersionUID = 599228653L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFavoriteTrack favoriteTrack = new QFavoriteTrack("favoriteTrack");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team.comma.domain.track.track.domain.QTrack track;

    public final com.team.comma.domain.user.user.domain.QUser user;

    public QFavoriteTrack(String variable) {
        this(FavoriteTrack.class, forVariable(variable), INITS);
    }

    public QFavoriteTrack(Path<? extends FavoriteTrack> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFavoriteTrack(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFavoriteTrack(PathMetadata metadata, PathInits inits) {
        this(FavoriteTrack.class, metadata, inits);
    }

    public QFavoriteTrack(Class<? extends FavoriteTrack> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.track = inits.isInitialized("track") ? new com.team.comma.domain.track.track.domain.QTrack(forProperty("track")) : null;
        this.user = inits.isInitialized("user") ? new com.team.comma.domain.user.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

