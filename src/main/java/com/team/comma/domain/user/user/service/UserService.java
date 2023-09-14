package com.team.comma.domain.user.user.service;

import com.team.comma.domain.user.history.dto.HistoryRequest;
import com.team.comma.domain.user.history.service.HistoryService;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.dto.LoginRequest;
import com.team.comma.domain.user.user.dto.RegisterRequest;
import com.team.comma.domain.user.user.dto.UserResponse;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.service.JwtService;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.*;
import static com.team.comma.global.jwt.support.CreationCookie.setCookieFromJwt;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HistoryService historyService;

    public MessageResponse login(final LoginRequest loginRequest , HttpServletResponse response)
        throws AccountException {
        User user = userRepository.findUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        if (user.getType() == UserType.OAUTH_USER) {
            throw new AccountException("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new UserException(NOT_FOUNT_USER);
        }

        setCookieFromJwt(response , jwtService.createJwtToken(user));

        return MessageResponse.of(LOGIN_SUCCESS , UserResponse.createUserResponse(user));
    }

    public MessageResponse register(final RegisterRequest registerRequest) throws AccountException {
        Optional<User> findUser = userRepository.findUserByEmail(registerRequest.getEmail());
        if(findUser.isPresent()) {
            throw new AccountException("이미 존재하는 계정입니다.");
        }

        User buildEntity = registerRequest.toUserEntity(UserType.GENERAL_USER);
        User user = userRepository.save(buildEntity);

        return MessageResponse.of(REGISTER_SUCCESS , UserResponse.createUserResponse(user));
    }

    public MessageResponse searchUserByNameAndNickName(String name , String accessToken) {
        List<User> userList = userRepository.searchUserByUserNameAndNickName(name);
        historyService.addHistory(HistoryRequest.builder().searchHistory(name).build() , accessToken);
        ArrayList<UserResponse> userResponses = new ArrayList<>();

        for(User user : userList) {
            userResponses.add(UserResponse.createUserResponse(user));
        }

        return MessageResponse.of(REQUEST_SUCCESS , userResponses);
    }

    public MessageResponse getUserByCookie(String token) {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        return MessageResponse.of(REQUEST_SUCCESS , UserResponse.createUserResponse(user));
    }

    public User findUserOrThrow(final String userEmail) {
        return userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));
    }
}
