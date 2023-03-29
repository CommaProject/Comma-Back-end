package com.team.comma.service.oauth;

import com.team.comma.dto.oauth.SignUpRequest;
import com.team.comma.entity.oauth.User;
import com.team.comma.enumType.Role;
import com.team.comma.exception.BadRequestException;
import com.team.comma.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthUserService {
    @Autowired OAuthRepository oAuthRepository;

    @Transactional
    public String createUser(SignUpRequest signUpRequest){
        if(oAuthRepository.existsByIdAndAuthProvider(signUpRequest.getId(), signUpRequest.getAuthProvider())){
            throw new BadRequestException("aleady exist user");
        }

        return oAuthRepository.save(
                User.builder()
                        .id(signUpRequest.getId())
                        .nickname(signUpRequest.getNickname())
                        .email(signUpRequest.getEmail())
                        .profileImageUrl(signUpRequest.getProfileImageUrl())
                        .role(Role.USER)
                        .authProvider(signUpRequest.getAuthProvider())
                        .build()).getId();
    }
}
