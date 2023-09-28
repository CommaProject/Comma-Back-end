package com.team.comma.domain.user.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.comma.domain.favorite.artist.dto.FavoriteArtistResponse;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.detail.domain.UserDetail;
import lombok.*;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserResponse {

    private long userId;
    private String email;
    private String password;
    private boolean delFlag;
    private UserRole role;
    private String name;
    private String nickName;
    private String profileImageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy/MM/dd" , timezone = "Asia/Seoul")
    private Date joinDate;

    public static UserResponse createUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .delFlag(user.getDelFlag())
                .role(user.getRole())
                .joinDate(user.getJoinDate())
                .profileImageUrl(user.getUserDetail().getProfileImageUrl())
                .name(user.getUserDetail().getName())
                .nickName(user.getUserDetail().getNickname())
                .build();
    }

    public static UserResponse buildUserResponse(String email){
        return UserResponse.builder()
                .userId(1L)
                .email(email)
                .password("password")
                .delFlag(false)
                .role(UserRole.USER)
                .name("name")
                .nickName("nickname")
                .profileImageUrl("url")
                .build();
    }

}
