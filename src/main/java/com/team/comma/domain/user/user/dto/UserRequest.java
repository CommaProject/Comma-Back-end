package com.team.comma.domain.user.user.dto;

import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserRequest {

    private String email;
    private String password;

    public User toUserEntity(final UserType userType , String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .type(userType)
                .role(UserRole.USER)
                .build();
    }

    public static UserRequest buildUserRequest(final String email) {
        return UserRequest.builder()
                .email(email)
                .password("password")
                .build();
    }

    public static UserRequest buildUserRequest(final String email , String password) {
        return UserRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public static UserRequest buildUserModifyRequest(final String password) {
        return UserRequest.builder()
                .password(password)
                .build();
    }

}
