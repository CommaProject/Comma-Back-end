package com.team.comma.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_detail_tb")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String sex;

    @Column(length = 5)
    private String age;

    @Column(length = 10)
    private LocalTime recommendTime;

    @Column(length = 10)
    private String nickname;

    @Column(length = 50)
    private String profileImageUrl;

    private Boolean popupAlertFlag;
    private Boolean favoritePublicFlag;

    private Boolean calenderPublicFlag;

    private Boolean allPublicFlag;
}
