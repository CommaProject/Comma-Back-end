package com.team.comma.domain.playlist.recommend.domain;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.recommend.constant.RecommendType;
import com.team.comma.domain.user.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import static com.team.comma.domain.playlist.recommend.constant.RecommendType.ANONYMOUS;
import static com.team.comma.domain.playlist.recommend.constant.RecommendType.FOLLOWING;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommend_tb")
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RecommendType recommendType;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ColumnDefault("0")
    private Long playCount;

    @JoinColumn(name = "playlist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Playlist playlist;

    @JoinColumn(name = "to_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User toUser;

    public void modifyPlayCount() {
        this.playCount += 1;
    }

    public static Recommend buildRecommend(Long id, RecommendType type, String comment, Playlist playlist, User toUser) {
        return Recommend.builder()
                .id(id)
                .recommendType(type)
                .comment(comment)
                .playCount(0L)
                .playlist(playlist)
                .toUser(toUser)
                .build();
    }

    public static Recommend createRecommend(String comment, Playlist playlist, User toUser) {
        return buildRecommend(null, FOLLOWING, comment, playlist, toUser);
    }

    public static Recommend createRecommend(String comment, Playlist playlist) {
        return buildRecommend(null, ANONYMOUS, comment, playlist, null);
    }

    public static Recommend createRecommend(long id, String comment, Playlist playlist, User toUser) {
        return buildRecommend(id, FOLLOWING, comment, playlist, toUser);
    }

    public static Recommend createRecommend(long id, String comment, Playlist playlist) {
        return buildRecommend(id, ANONYMOUS, comment, playlist, null);
    }

}
