package com.team.comma.domain.track.track.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrack is a Querydsl query type for Track
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrack extends EntityPathBase<Track> {

    private static final long serialVersionUID = -281366700L;

    public static final QTrack track = new QTrack("track");

    public final StringPath albumImageUrl = createString("albumImageUrl");

    public final NumberPath<Integer> durationTimeMs = createNumber("durationTimeMs", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> recommendCount = createNumber("recommendCount", Long.class);

    public final StringPath spotifyTrackHref = createString("spotifyTrackHref");

    public final StringPath spotifyTrackId = createString("spotifyTrackId");

    public final ListPath<com.team.comma.domain.track.artist.domain.TrackArtist, com.team.comma.domain.track.artist.domain.QTrackArtist> trackArtistList = this.<com.team.comma.domain.track.artist.domain.TrackArtist, com.team.comma.domain.track.artist.domain.QTrackArtist>createList("trackArtistList", com.team.comma.domain.track.artist.domain.TrackArtist.class, com.team.comma.domain.track.artist.domain.QTrackArtist.class, PathInits.DIRECT2);

    public final StringPath trackTitle = createString("trackTitle");

    public QTrack(String variable) {
        super(Track.class, forVariable(variable));
    }

    public QTrack(Path<? extends Track> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTrack(PathMetadata metadata) {
        super(Track.class, metadata);
    }

}

