package com.team.comma.domain.playlist.playlist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlaylist is a Querydsl query type for Playlist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlaylist extends EntityPathBase<Playlist> {

    private static final long serialVersionUID = 1720285295L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlaylist playlist = new QPlaylist("playlist");

    public final BooleanPath alarmFlag = createBoolean("alarmFlag");

    public final TimePath<java.time.LocalTime> alarmStartTime = createTime("alarmStartTime", java.time.LocalTime.class);

    public final BooleanPath delFlag = createBoolean("delFlag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath playlistTitle = createString("playlistTitle");

    public final ListPath<com.team.comma.domain.playlist.track.domain.PlaylistTrack, com.team.comma.domain.playlist.track.domain.QPlaylistTrack> playlistTrackList = this.<com.team.comma.domain.playlist.track.domain.PlaylistTrack, com.team.comma.domain.playlist.track.domain.QPlaylistTrack>createList("playlistTrackList", com.team.comma.domain.playlist.track.domain.PlaylistTrack.class, com.team.comma.domain.playlist.track.domain.QPlaylistTrack.class, PathInits.DIRECT2);

    public final com.team.comma.domain.user.user.domain.QUser user;

    public QPlaylist(String variable) {
        this(Playlist.class, forVariable(variable), INITS);
    }

    public QPlaylist(Path<? extends Playlist> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlaylist(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlaylist(PathMetadata metadata, PathInits inits) {
        this(Playlist.class, metadata, inits);
    }

    public QPlaylist(Class<? extends Playlist> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.team.comma.domain.user.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

