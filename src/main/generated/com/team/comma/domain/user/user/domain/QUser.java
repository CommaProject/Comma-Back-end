package com.team.comma.domain.user.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1860799880L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final BooleanPath delFlag = createBoolean("delFlag");

    public final StringPath email = createString("email");

    public final ListPath<com.team.comma.domain.favorite.artist.domain.FavoriteArtist, com.team.comma.domain.favorite.artist.domain.QFavoriteArtist> favoriteArtist = this.<com.team.comma.domain.favorite.artist.domain.FavoriteArtist, com.team.comma.domain.favorite.artist.domain.QFavoriteArtist>createList("favoriteArtist", com.team.comma.domain.favorite.artist.domain.FavoriteArtist.class, com.team.comma.domain.favorite.artist.domain.QFavoriteArtist.class, PathInits.DIRECT2);

    public final ListPath<com.team.comma.domain.user.history.domain.History, com.team.comma.domain.user.history.domain.QHistory> history = this.<com.team.comma.domain.user.history.domain.History, com.team.comma.domain.user.history.domain.QHistory>createList("history", com.team.comma.domain.user.history.domain.History.class, com.team.comma.domain.user.history.domain.QHistory.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.util.Date> joinDate = createDate("joinDate", java.util.Date.class);

    public final StringPath password = createString("password");

    public final EnumPath<com.team.comma.domain.user.user.constant.UserRole> role = createEnum("role", com.team.comma.domain.user.user.constant.UserRole.class);

    public final EnumPath<com.team.comma.domain.user.user.constant.UserType> type = createEnum("type", com.team.comma.domain.user.user.constant.UserType.class);

    public final com.team.comma.domain.user.detail.domain.QUserDetail userDetail;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userDetail = inits.isInitialized("userDetail") ? new com.team.comma.domain.user.detail.domain.QUserDetail(forProperty("userDetail"), inits.get("userDetail")) : null;
    }

}

