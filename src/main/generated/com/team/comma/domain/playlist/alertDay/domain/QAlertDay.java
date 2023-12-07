package com.team.comma.domain.playlist.alertDay.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlertDay is a Querydsl query type for AlertDay
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlertDay extends EntityPathBase<AlertDay> {

    private static final long serialVersionUID = -1726316021L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlertDay alertDay = new QAlertDay("alertDay");

    public final EnumPath<java.time.DayOfWeek> alarmDay = createEnum("alarmDay", java.time.DayOfWeek.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team.comma.domain.playlist.playlist.domain.QPlaylist playlist;

    public QAlertDay(String variable) {
        this(AlertDay.class, forVariable(variable), INITS);
    }

    public QAlertDay(Path<? extends AlertDay> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlertDay(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlertDay(PathMetadata metadata, PathInits inits) {
        this(AlertDay.class, metadata, inits);
    }

    public QAlertDay(Class<? extends AlertDay> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.playlist = inits.isInitialized("playlist") ? new com.team.comma.domain.playlist.playlist.domain.QPlaylist(forProperty("playlist"), inits.get("playlist")) : null;
    }

}

