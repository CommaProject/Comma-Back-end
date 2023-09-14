package com.team.comma.domain.user.detail.dto;

import com.team.comma.domain.user.detail.domain.UserDetail;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDetailRequest {

    private String nickName;
    private String profileImageUrl;
    private Boolean popupAlertFlag;
    private Boolean favoritePublicFlag;
    private Boolean calenderPublicFlag;
    private Boolean allPublicFlag;

    public UserDetail toUserDetailEntity() {
        return UserDetail.builder()
                .nickname(nickName)
                .profileImageUrl(profileImageUrl)
                .popupAlertFlag(popupAlertFlag)
                .favoritePublicFlag(favoritePublicFlag)
                .calenderPublicFlag(calenderPublicFlag)
                .allPublicFlag(allPublicFlag)
                .build();
    }

    public static UserDetailRequest buildUserDetailRequest() {
        return UserDetailRequest.builder()
                .nickName("name")
                .profileImageUrl("profileImageUrl")
                .build();
    }

}
