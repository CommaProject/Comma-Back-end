package com.team.comma.domain.user.detail.domain;

import com.team.comma.global.converter.BooleanConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "user_detail_tb")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45)
    private String name;

    @Column(length = 45)
    private String nickname;

    @Column(length = 100)
    private String profileImageUrl;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean popupAlertFlag = true;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean favoritePublicFlag = true;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean calenderPublicFlag = true;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean allPublicFlag = true;

    public static UserDetail buildUserDetail() {
        return UserDetail.builder()
                .name("name")
                .nickname("nickname")
                .profileImageUrl("S3 url")
                .build();
    }

    public void modifyProfileImage(final String url){
        this.profileImageUrl = url;
    }

}



