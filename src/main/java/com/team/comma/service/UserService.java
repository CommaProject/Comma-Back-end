package com.team.comma.service;

import java.util.Collections;

import javax.security.auth.login.AccountException;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.RequestUserDTO;
import com.team.comma.dto.TokenDTO;
import com.team.comma.entity.Token;
import com.team.comma.entity.UserEntity;
import com.team.comma.repository.UserRepository;
import com.team.comma.security.CreateCookie;
import com.team.comma.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	final private UserRepository userRepository;
	final private JwtService jwtService;
	final private JwtTokenProvider jwtTokenProvider;

	public TokenDTO login(RequestUserDTO userDTO) throws AccountException {
		UserEntity userEntity = userRepository.findByEmail(userDTO.getEmail());

		if (userEntity == null || userEntity.getPassword() != userDTO.getPassword()) {
			throw new AccountException("정보가 올바르지 않습니다.");
		}

		Token tokenDTO = jwtTokenProvider.createAccessToken(userEntity.getUsername(), userEntity.getRoles());
		jwtService.login(tokenDTO);
		
		ServletRequestAttributes attr = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
		HttpServletResponse response = attr.getResponse();
		
		response.addCookie(CreateCookie.createRefreshToken(tokenDTO.getRefreshToken()));

		return TokenDTO.builder().code(1).id(userDTO.getEmail()).accessToken(tokenDTO.getAccessToken())
				.grandType(tokenDTO.getGrantType()).build();
	}

	public MessageDTO register(RequestUserDTO userDTO) throws AccountException {
		UserEntity userEntity = userRepository.findByEmail(userDTO.getEmail());

		if (userEntity != null) {
			throw new AccountException("이미 있는 계정입니다.");
		}

		UserEntity buildEntity = UserEntity.builder().email(userDTO.getEmail()).name(userDTO.getName())
				.sex(userDTO.getSex()).password(userDTO.getPassword()).age(userDTO.getAge())
				.roles(Collections.singletonList("ROLE_USER")).recommandTime(userDTO.getRecommandTime())
				.isLeave(userDTO.getIsLeave()).build();

		userRepository.save(buildEntity);

		return MessageDTO.builder().code(1).message("성공적으로 가입되었습니다.").build();
	}

}
