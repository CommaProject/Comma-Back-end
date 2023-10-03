package com.team.comma.domain.user.detail.dto;

import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.domain.User;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDetailRequest {

    private String nickname;
    private String profileImageUrl;
    private Boolean popupAlertFlag;
    private Boolean favoritePublicFlag;
    private Boolean calenderPublicFlag;
    private Boolean allPublicFlag;

    public UserDetail toUserDetailEntity(User user) {
        return UserDetail.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .popupAlertFlag(popupAlertFlag)
                .favoritePublicFlag(favoritePublicFlag)
                .calenderPublicFlag(calenderPublicFlag)
                .allPublicFlag(allPublicFlag)
                .user(user)
                .build();
    }

    public static UserDetailRequest buildUserDetailCreateRequest() {
        return UserDetailRequest.builder()
                .nickname("nickname")
                .build();
    }

    public static UserDetailRequest buildUserDetailModifyRequest() {
        return UserDetailRequest.builder()
                .nickname("nickname")
                .profileImageUrl("profileImageUrl")
                .popupAlertFlag(true)
                .favoritePublicFlag(true)
                .calenderPublicFlag(true)
                .allPublicFlag(true)
                .build();
    }

}
