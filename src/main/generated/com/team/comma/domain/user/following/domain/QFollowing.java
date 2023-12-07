package com.team.comma.domain.user.following.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFollowing is a Querydsl query type for Following
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFollowing extends EntityPathBase<Following> {

    private static final long serialVersionUID = -477459366L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFollowing following = new QFollowing("following");

    public final BooleanPath blockFlag = createBoolean("blockFlag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team.comma.domain.user.user.domain.QUser userFrom;

    public final com.team.comma.domain.user.user.domain.QUser userTo;

    public QFollowing(String variable) {
        this(Following.class, forVariable(variable), INITS);
    }

    public QFollowing(Path<? extends Following> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFollowing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFollowing(PathMetadata metadata, PathInits inits) {
        this(Following.class, metadata, inits);
    }

    public QFollowing(Class<? extends Following> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userFrom = inits.isInitialized("userFrom") ? new com.team.comma.domain.user.user.domain.QUser(forProperty("userFrom"), inits.get("userFrom")) : null;
        this.userTo = inits.isInitialized("userTo") ? new com.team.comma.domain.user.user.domain.QUser(forProperty("userTo"), inits.get("userTo")) : null;
    }

}

