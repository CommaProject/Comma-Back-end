package com.team.comma.domain.playlist.recommend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecommend is a Querydsl query type for Recommend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecommend extends EntityPathBase<Recommend> {

    private static final long serialVersionUID = -514685997L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecommend recommend = new QRecommend("recommend");

    public final StringPath comment = createString("comment");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> playCount = createNumber("playCount", Long.class);

    public final com.team.comma.domain.playlist.playlist.domain.QPlaylist playlist;

    public final EnumPath<com.team.comma.domain.playlist.recommend.constant.RecommendType> recommendType = createEnum("recommendType", com.team.comma.domain.playlist.recommend.constant.RecommendType.class);

    public final com.team.comma.domain.user.user.domain.QUser toUser;

    public QRecommend(String variable) {
        this(Recommend.class, forVariable(variable), INITS);
    }

    public QRecommend(Path<? extends Recommend> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecommend(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecommend(PathMetadata metadata, PathInits inits) {
        this(Recommend.class, metadata, inits);
    }

    public QRecommend(Class<? extends Recommend> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.playlist = inits.isInitialized("playlist") ? new com.team.comma.domain.playlist.playlist.domain.QPlaylist(forProperty("playlist"), inits.get("playlist")) : null;
        this.toUser = inits.isInitialized("toUser") ? new com.team.comma.domain.user.user.domain.QUser(forProperty("toUser"), inits.get("toUser")) : null;
    }

}

