package com.team.comma.domain.artist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QArtist is a Querydsl query type for Artist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArtist extends EntityPathBase<Artist> {

    private static final long serialVersionUID = 101379703L;

    public static final QArtist artist = new QArtist("artist");

    public final StringPath artistImageUrl = createString("artistImageUrl");

    public final StringPath artistName = createString("artistName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath spotifyArtistId = createString("spotifyArtistId");

    public QArtist(String variable) {
        super(Artist.class, forVariable(variable));
    }

    public QArtist(Path<? extends Artist> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArtist(PathMetadata metadata) {
        super(Artist.class, metadata);
    }

}

