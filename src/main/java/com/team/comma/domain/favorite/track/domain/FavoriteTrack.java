package com.team.comma.domain.favorite.track.domain;

import com.team.comma.domain.track.track.domain.Track;
import com.team.comma.domain.user.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite_track_tb")
public class FavoriteTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "track_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Track track;

    public static FavoriteTrack buildFavoriteTrack(User user , Track track) {
        return FavoriteTrack.builder()
                .user(user)
                .track(track)
                .build();
    }

}
