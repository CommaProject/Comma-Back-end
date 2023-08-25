package com.team.comma.domain.playlist.archive.domain;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.user.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "archive_tb")
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean publicFlag;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // user 삭제 필요 - 이미 playlist에 user와 연관관계가 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    public static Archive buildArchive(User user, String context, Playlist playlist) {
        return Archive.builder()
                .content(context)
                .publicFlag(true)
                .user(user)
                .playlist(playlist)
                .build();
    }

}