package com.team.comma.domain.user.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;
}
