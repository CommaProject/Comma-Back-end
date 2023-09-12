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
    private List<String> artistNames;

    public UserDetail toUserDetailEntity() {
        return UserDetail.builder()
                .nickname(nickName)
                .build();
    }

    public static UserDetailRequest buildUserDetailRequest() {
        return UserDetailRequest.builder()
                .nickName("name")
                .build();
    }
}
