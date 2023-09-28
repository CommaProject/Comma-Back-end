package com.team.comma.domain.user.user.service;

import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.dto.UserRequest;
import com.team.comma.domain.user.user.dto.UserResponse;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.jwt.service.JwtService;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.constant.ResponseCodeEnum.*;
import static com.team.comma.global.jwt.support.CreationCookie.setCookieFromJwt;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public MessageResponse login(final UserRequest loginRequest, HttpServletResponse response)
        throws AccountException {
        User user = userRepository.findUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        if (user.getType() == UserType.OAUTH_USER) {
            throw new AccountException("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");
        }

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserException(NOT_FOUNT_USER);
        }

        setCookieFromJwt(response, jwtService.createJwtToken(user));

        return MessageResponse.of(LOGIN_SUCCESS, UserResponse.of(user));
    }

    @Transactional
    public MessageResponse register(final UserRequest userRequest) throws AccountException {
        findUserThenThrow(userRequest.getEmail());

        String encodedPassword = bCryptPasswordEncoder.encode(userRequest.getPassword());
        User buildEntity = userRequest.toUserEntity(UserType.GENERAL_USER , encodedPassword);
        User user = userRepository.save(buildEntity);

        return MessageResponse.of(REGISTER_SUCCESS, UserResponse.of(user));
    }

    public void findUserThenThrow(final String email) throws AccountException {
        Optional<User> findUser = userRepository.findUserByEmail(email);
        if(findUser.isPresent()) {
            throw new AccountException("이미 존재하는 계정입니다.");
        }
    }

    public MessageResponse getUserInformation(final String token) {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = findUserOrThrow(userEmail);

        return MessageResponse.of(REQUEST_SUCCESS, UserResponse.of(user));
    }

    public MessageResponse findAllUsersBySearchWord(final String searchWord, final String accessToken) throws AccountException {
        if (accessToken == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        List<User> userList = userRepository.findAllUsersByNameAndNickName(searchWord);
        ArrayList<UserResponse> userResponses = new ArrayList<>();

        for(User user : userList) {
            userResponses.add(UserResponse.of(user));
        }

        return MessageResponse.of(REQUEST_SUCCESS, userResponses);
    }

    public User findUserOrThrow(final String userEmail) {
        return userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));
    }

    @Transactional
    public MessageResponse modifyUserPassword(final String accessToken, final String password) throws AccountException {
        if (accessToken == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User user = findUserOrThrow(userEmail);
        user.modifyPassword(bCryptPasswordEncoder.encode(password));

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}
