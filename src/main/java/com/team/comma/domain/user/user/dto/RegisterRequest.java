package com.team.comma.domain.user.user.dto;

import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;

    public User toUserEntity(final UserType userType) {
        return User.builder()
                .email(email)
                .password(password)
                .type(userType)
                .role(UserRole.USER)
                .build();
    }

}
