package com.team.comma.domain.user.detail.domain;

import com.team.comma.domain.user.detail.dto.UserDetailRequest;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.global.converter.BooleanConverter;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static UserDetail buildUserDetail(User user) {
        UserDetail detail = UserDetail.builder()
                .name("name")
                .nickname("nickname")
                .profileImageUrl("S3 url")
                .user(user)
                .build();

        user.setUserDetail(detail);
        return detail;
    }

    public void modifyUserDetail(final UserDetailRequest request){
        this.nickname = request.getNickname() != null ? request.getNickname() : this.nickname ;
        this.profileImageUrl = request.getProfileImageUrl() != null ? request.getProfileImageUrl() : this.profileImageUrl;
        this.popupAlertFlag = request.getPopupAlertFlag() != null ? request.getPopupAlertFlag() : this.popupAlertFlag;
        this.favoritePublicFlag = request.getFavoritePublicFlag() != null ? request.getFavoritePublicFlag() : this.favoritePublicFlag;
        this.calenderPublicFlag = request.getCalenderPublicFlag() != null ? request.getCalenderPublicFlag() : this.calenderPublicFlag;
        this.allPublicFlag = request.getAllPublicFlag() != null ? request.getAllPublicFlag() : this.allPublicFlag;
    }

}
