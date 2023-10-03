SET foreign_key_checks = 0;

drop table if exists alert_day_tb;

drop table if exists archive_tb;

drop table if exists artist_tb;

drop table if exists favorite_artist_tb;

drop table if exists favorite_track_tb;

drop table if exists following_tb;

drop table if exists history_tb;

drop table if exists playlist_tb;

drop table if exists playlist_track_tb;

drop table if exists recommend_tb;

drop table if exists refresh_token_tb;

drop table if exists track_artist_tb;

drop table if exists track_play_count_tb;

drop table if exists track_tb;

drop table if exists user_detail_tb;

drop table if exists user_tb;

create table alert_day_tb
(
    id          bigint not null auto_increment,
    alarm_day   smallint,
    playlist_id bigint,
    primary key (id)
);

create table archive_tb
(
    id          bigint not null auto_increment,
    comment     TEXT,
    create_date datetime(6),
    public_flag bit,
    playlist_id bigint,
    user_id     bigint,
    primary key (id)
);

create table artist_tb
(
    id                bigint       not null auto_increment,
    artist_image_url  varchar(100),
    artist_name       varchar(30),
    spotify_artist_id varchar(255) not null,
    primary key (id)
);

create table favorite_artist_tb
(
    id        bigint not null auto_increment,
    artist_id bigint,
    user_id   bigint,
    primary key (id)
);

create table favorite_track_tb
(
    id       bigint not null auto_increment,
    track_id bigint,
    user_id  bigint,
    primary key (id)
);

create table following_tb
(
    id         bigint not null auto_increment,
    block_flag bit,
    user_from  bigint,
    user_to    bigint,
    primary key (id)
);

create table history_tb
(
    id             bigint       not null auto_increment,
    del_flag       varchar(255) not null,
    search_history varchar(50),
    user_id        bigint,
    primary key (id)
);

create table playlist_tb
(
    id               bigint       not null auto_increment,
    alarm_flag       bit default false,
    alarm_start_time time,
    del_flag         varchar(255) not null,
    playlist_title   varchar(100),
    user_id          bigint,
    primary key (id)
);

create table playlist_track_tb
(
    id               bigint not null auto_increment,
    play_sequence    integer,
    track_alarm_flag bit default false,
    playlist_id      bigint,
    track_id         bigint,
    primary key (id)
);

create table recommend_tb
(
    id             bigint not null auto_increment,
    comment        TEXT,
    play_count     bigint default 0,
    recommend_type varchar(255),
    playlist_id    bigint,
    to_user_id     bigint,
    primary key (id)
);

create table refresh_token_tb
(
    id        bigint       not null auto_increment,
    key_email varchar(255) not null,
    token     varchar(255) not null,
    primary key (id)
);

create table track_artist_tb
(
    id        bigint not null auto_increment,
    artist_id bigint,
    track_id  bigint,
    primary key (id)
);

create table track_play_count_tb
(
    id       bigint not null auto_increment,
    track_id bigint,
    user_id  bigint,
    primary key (id)
);

create table track_tb
(
    id                 bigint       not null auto_increment,
    album_image_url    varchar(100) not null,
    duration_time_ms   integer,
    recommend_count    bigint,
    spotify_track_href varchar(150) not null,
    spotify_track_id   varchar(100) not null,
    track_title        varchar(150) not null,
    primary key (id)
);

create table user_detail_tb
(
    id                   bigint not null auto_increment,
    all_public_flag      varchar(255),
    calender_public_flag varchar(255),
    favorite_public_flag varchar(255),
    name                 varchar(45),
    nickname             varchar(45),
    popup_alert_flag     varchar(255),
    profile_image_url    varchar(100),
    user_id              bigint,
    primary key (id)
);

create table user_tb
(
    id        bigint       not null auto_increment,
    del_flag  varchar(255),
    email     varchar(100) not null,
    join_date date         not null,
    password  varchar(100),
    role      varchar(255),
    type      varchar(255),
    primary key (id)
);

alter table alert_day_tb
    add constraint FKc6g58jfqla62kp6oj86mc40f5
        foreign key (playlist_id)
            references playlist_tb (id);

alter table archive_tb
    add constraint FK4ak904pbmisqcoklwmhxco1dk
        foreign key (playlist_id)
            references playlist_tb (id);

alter table archive_tb
    add constraint FK89mrmnccnyaf8no9fqo39m8pi
        foreign key (user_id)
            references user_tb (id);

alter table favorite_artist_tb
    add constraint FKrs3m7n6ra8dkstmepdepw3orq
        foreign key (artist_id)
            references artist_tb (id);

alter table favorite_artist_tb
    add constraint FKldtp2cwjk9xdaangm3eiial2j
        foreign key (user_id)
            references user_tb (id);

alter table favorite_track_tb
    add constraint FKtolq4dya9xdvj9htinkqq5ubj
        foreign key (track_id)
            references track_tb (id);

alter table favorite_track_tb
    add constraint FKpk0a3b8w7h23tm5uwaoipvle
        foreign key (user_id)
            references user_tb (id);

alter table following_tb
    add constraint FKhmners73nruj9vfyxtgsfj4vt
        foreign key (user_from)
            references user_tb (id);

alter table following_tb
    add constraint FKk9y68jl4wgxfc755m806t04tb
        foreign key (user_to)
            references user_tb (id);

alter table history_tb
    add constraint FK5im8h3bb9pql3s0mbbaks3x1g
        foreign key (user_id)
            references user_tb (id);

alter table playlist_tb
    add constraint FKdvl5wdodfgpw3o52q6uyp3rnx
        foreign key (user_id)
            references user_tb (id);

alter table playlist_track_tb
    add constraint FKjvhcm6c5o1pcubj641m80oj9p
        foreign key (playlist_id)
            references playlist_tb (id);

alter table playlist_track_tb
    add constraint FKalqct1m2bi3yyfu1sia9bpeoh
        foreign key (track_id)
            references track_tb (id);

alter table recommend_tb
    add constraint FKj4n9pv3qdg5aa5qkwn25l2ag8
        foreign key (playlist_id)
            references playlist_tb (id);

alter table recommend_tb
    add constraint FKqk882bavc4v17cdsv1uam4lek
        foreign key (to_user_id)
            references user_tb (id);

alter table track_artist_tb
    add constraint FKg56ydenmm6fv1686ubd4potfn
        foreign key (artist_id)
            references artist_tb (id);

alter table track_artist_tb
    add constraint FKgww0by0drg0sw8t3ybxdln14l
        foreign key (track_id)
            references track_tb (id);

alter table track_play_count_tb
    add constraint FKpjpxo9bbqrnsyidmi3y3km2ev
        foreign key (track_id)
            references track_tb (id);

alter table track_play_count_tb
    add constraint FKjhwh31fvujf0ya10gntaf3kfh
        foreign key (user_id)
            references user_tb (id);

alter table user_detail_tb
    add constraint FKi63mdcfkuycrt2itishp0v320
        foreign key (user_id)
            references user_tb (id);

SET foreign_key_checks = 1;