package com.team.comma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@Table
@NoArgsConstructor
@AllArgsConstructor
public class UserPlaylist {

    @Id
    @GeneratedValue
    private Long playlistKey;

    @Column(length = 100, nullable = false)
    private String playlistName;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.PERSIST)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "userPlaylist" , cascade = CascadeType.PERSIST , orphanRemoval = true)
    private List<MusicEntity> musicEntity;

    @Column(length = 1, nullable = false)
    private String alarmYn;

    @Column(length = 2, nullable = false)
    private String alarmSetDay;

    @Column(length = 4, nullable = false)
    private String alarmStartTime;

    @Column(length = 4, nullable = false)
    private String alarmEndTime;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime registDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifyDate;

}
