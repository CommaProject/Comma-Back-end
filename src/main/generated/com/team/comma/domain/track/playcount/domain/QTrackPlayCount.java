package com.team.comma.domain.track.playcount.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrackPlayCount is a Querydsl query type for TrackPlayCount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrackPlayCount extends EntityPathBase<TrackPlayCount> {

    private static final long serialVersionUID = -1236191657L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrackPlayCount trackPlayCount = new QTrackPlayCount("trackPlayCount");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team.comma.domain.track.track.domain.QTrack track;

    public final com.team.comma.domain.user.user.domain.QUser user;

    public QTrackPlayCount(String variable) {
        this(TrackPlayCount.class, forVariable(variable), INITS);
    }

    public QTrackPlayCount(Path<? extends TrackPlayCount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrackPlayCount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrackPlayCount(PathMetadata metadata, PathInits inits) {
        this(TrackPlayCount.class, metadata, inits);
    }

    public QTrackPlayCount(Class<? extends TrackPlayCount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.track = inits.isInitialized("track") ? new com.team.comma.domain.track.track.domain.QTrack(forProperty("track")) : null;
        this.user = inits.isInitialized("user") ? new com.team.comma.domain.user.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

