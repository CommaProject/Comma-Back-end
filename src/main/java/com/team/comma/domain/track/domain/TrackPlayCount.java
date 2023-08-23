package com.team.comma.domain.track.domain;

import com.team.comma.domain.track.dto.TrackPlayCountRequest;
import com.team.comma.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "track_play_count_tb")
public class TrackPlayCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Integer playCount = 1;

    private String trackId;

    private String trackImageUrl;

    private String trackName;

    private String trackArtist;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.PERSIST)
    private User user;

    public static TrackPlayCount createTrackPlayCount(TrackPlayCountRequest trackPlayCountRequest , User user) {
        return TrackPlayCount.builder()
                .trackId(trackPlayCountRequest.getTrackId())
                .trackImageUrl(trackPlayCountRequest.getTrackImageUrl())
                .trackName(trackPlayCountRequest.getTrackName())
                .trackArtist(trackPlayCountRequest.getTrackArtist())
                .user(user)
                .build();
    }

    public void updatePlayCount() {
        this.playCount += 1;
    }

}
