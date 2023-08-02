package com.team.comma.spotify.track.service;

import com.team.comma.common.constant.ResponseCodeEnum;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MessageResponse countPlayCount(String accessToken) {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}
