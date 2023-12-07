package com.team.comma.domain.playlist.track.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlaylistTrack is a Querydsl query type for PlaylistTrack
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlaylistTrack extends EntityPathBase<PlaylistTrack> {

    private static final long serialVersionUID = -1574219647L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlaylistTrack playlistTrack = new QPlaylistTrack("playlistTrack");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team.comma.domain.playlist.playlist.domain.QPlaylist playlist;

    public final NumberPath<Integer> playSequence = createNumber("playSequence", Integer.class);

    public final com.team.comma.domain.track.track.domain.QTrack track;

    public final BooleanPath trackAlarmFlag = createBoolean("trackAlarmFlag");

    public QPlaylistTrack(String variable) {
        this(PlaylistTrack.class, forVariable(variable), INITS);
    }

    public QPlaylistTrack(Path<? extends PlaylistTrack> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlaylistTrack(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlaylistTrack(PathMetadata metadata, PathInits inits) {
        this(PlaylistTrack.class, metadata, inits);
    }

    public QPlaylistTrack(Class<? extends PlaylistTrack> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.playlist = inits.isInitialized("playlist") ? new com.team.comma.domain.playlist.playlist.domain.QPlaylist(forProperty("playlist"), inits.get("playlist")) : null;
        this.track = inits.isInitialized("track") ? new com.team.comma.domain.track.track.domain.QTrack(forProperty("track")) : null;
    }

}

