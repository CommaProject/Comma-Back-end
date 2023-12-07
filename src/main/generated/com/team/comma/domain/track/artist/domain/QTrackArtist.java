package com.team.comma.domain.track.artist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrackArtist is a Querydsl query type for TrackArtist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrackArtist extends EntityPathBase<TrackArtist> {

    private static final long serialVersionUID = -2019297761L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrackArtist trackArtist = new QTrackArtist("trackArtist");

    public final com.team.comma.domain.artist.domain.QArtist artist;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team.comma.domain.track.track.domain.QTrack track;

    public QTrackArtist(String variable) {
        this(TrackArtist.class, forVariable(variable), INITS);
    }

    public QTrackArtist(Path<? extends TrackArtist> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrackArtist(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrackArtist(PathMetadata metadata, PathInits inits) {
        this(TrackArtist.class, metadata, inits);
    }

    public QTrackArtist(Class<? extends TrackArtist> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.artist = inits.isInitialized("artist") ? new com.team.comma.domain.artist.domain.QArtist(forProperty("artist")) : null;
        this.track = inits.isInitialized("track") ? new com.team.comma.domain.track.track.domain.QTrack(forProperty("track")) : null;
    }

}

