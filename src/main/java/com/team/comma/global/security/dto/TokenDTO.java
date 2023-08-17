package com.team.comma.global.security.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TokenDTO {

    int code;
    String id;
    String accessToken;
    String grandType;
}
