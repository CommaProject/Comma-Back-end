package com.team.comma.domain.user.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy/MM/dd" , timezone = "Asia/Seoul")
    private Date joinDate;
    private String profileImage;

    public static UserResponse createUserResponse(User user) {
        String profileImage = null;
        String name = null;
        String nickName = null;
        UserDetail detail = user.getUserDetail();
        if(detail != null) {
            profileImage = detail.getProfileImageUrl();
            name = detail.getName();
            nickName = detail.getNickname();
        }

        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .delFlag(user.getDelFlag())
                .role(user.getRole())
                .profileImage(profileImage)
                .name(name)
                .nickName(nickName)
                .joinDate(user.getJoinDate())
                .build();
    }

}
