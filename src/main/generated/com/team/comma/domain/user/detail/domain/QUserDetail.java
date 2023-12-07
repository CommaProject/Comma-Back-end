package com.team.comma.domain.user.detail.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserDetail is a Querydsl query type for UserDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserDetail extends EntityPathBase<UserDetail> {

    private static final long serialVersionUID = 1824916863L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserDetail userDetail = new QUserDetail("userDetail");

    public final BooleanPath allPublicFlag = createBoolean("allPublicFlag");

    public final BooleanPath calenderPublicFlag = createBoolean("calenderPublicFlag");

    public final BooleanPath favoritePublicFlag = createBoolean("favoritePublicFlag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final BooleanPath popupAlertFlag = createBoolean("popupAlertFlag");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final com.team.comma.domain.user.user.domain.QUser user;

    public QUserDetail(String variable) {
        this(UserDetail.class, forVariable(variable), INITS);
    }

    public QUserDetail(Path<? extends UserDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserDetail(PathMetadata metadata, PathInits inits) {
        this(UserDetail.class, metadata, inits);
    }

    public QUserDetail(Class<? extends UserDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.team.comma.domain.user.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

