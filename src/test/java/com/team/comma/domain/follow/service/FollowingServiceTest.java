package com.team.comma.domain.follow.service;

import com.team.comma.domain.user.followship.constant.FollowingType;
import com.team.comma.domain.user.userDetail.UserDetail;
import com.team.comma.domain.user.followship.service.FollowingService;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.user.followship.domain.Following;
import com.team.comma.domain.user.followship.dto.FollowingResponse;
import com.team.comma.domain.user.followship.exception.FollowingException;
import com.team.comma.domain.user.followship.repository.FollowingRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class FollowingServiceTest {
    @InjectMocks
    FollowingService followingService;

    @Mock
    FollowingRepository followingRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("사용자 차단 성공")
    public void blockUserSuccess() {
        // given
        doNothing().when(followingRepository).blockFollowedUser("toUserEmail" , "toUserEmail");
        doReturn("toUserEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = followingService.blockFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("사용자 차단해제 성공")
    public void unblockUserSuccess() {
        // given
        doNothing().when(followingRepository).unblockFollowedUser("toUserEmail" , "toUserEmail");
        doReturn("toUserEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = followingService.unblockFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 이미 팔로우 한 사용자")
    public void followFail_existFollowUser() {
        // given
        doReturn(Optional.of(User.builder().build())).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "toUserEmail");
        doReturn("toUserEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(FollowingException.class).hasMessage("이미 팔로우중인 사용자입니다.");
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 사용자 조회 실패")
    public void followFail_notFoundToUser() {
        // given
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(userRepository).findByEmail("toUserEmail");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("해당 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 대상 사용자 조회 실패")
    public void followFail_notFoundFromUser() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("toUserEmail");
        doReturn(Optional.empty()).when(userRepository).findByEmail("fromUserEmail");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("대상 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 차단된 사용자")
    public void followFail_isBlockedUser() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");
        doReturn(Optional.of(User.builder().build())).when(followingRepository).getBlockedUser("toUserEmail" , "fromUserEmail");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(FollowingException.class).hasMessage("차단된 사용자입니다.");
    }

    @Test
    @DisplayName("팔로우 여부 확인 _ 거짓")
    public void isfollow_false() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");

        // when
        MessageResponse result = followingService.isFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getData()).isEqualTo(false);
    }

    @Test
    @DisplayName("팔로우 여부 확인 _ 참")
    public void isfollow_true() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");

        // when
        MessageResponse result = followingService.isFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getData()).isEqualTo(true);
    }
    @Test
    public void 팔로잉_리스트_조회_실패_사용자_찾을수없음() throws AccountException {
        // given
        String token = "accessToken";

        // when
        Throwable thrown = catchThrowable(() -> followingService.getFollowingUserList(token, FollowingType.FOLLOWING));

        // then
        assertThat(thrown.getMessage()).isEqualTo("사용자 정보를 찾을 수 없습니다.");

    }

    @Test
    public void 팔로잉_리스트_조회() throws AccountException {
        // given
        String token = "accessToken";

        User user = buildUserWithIdAndEmail(1L, "user");
        User targetUser = buildUserWithIdAndEmail(2L, "targetUser");
        doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);

        Following following = buildFollowingWithIdAndUserFromTo(1L, user, targetUser);
        FollowingResponse followingResponse = FollowingResponse.of(following, FollowingType.FOLLOWING);
        doReturn(List.of(followingResponse)).when(followingRepository).getFollowingToUserListByFromUser(user);

        // when
        MessageResponse result = followingService.getFollowingUserList(token, FollowingType.FOLLOWING);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(List.of(followingResponse));

    }

    @Test
    public void 팔로워_리스트_조회() throws AccountException {
        // given
        String token = "accessToken";

        User user = buildUserWithIdAndEmail(1L, "user");
        User targetUser = buildUserWithIdAndEmail(2L, "targetUser");

        doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);

        Following followed = buildFollowingWithIdAndUserFromTo(1L, targetUser, user);
        FollowingResponse followedResponse = FollowingResponse.of(followed, FollowingType.FOLLOWED);
        doReturn(List.of(followedResponse)).when(followingRepository).getFollowingFromUserListByToUser(user);

        // when
        MessageResponse result = followingService.getFollowingUserList(token, FollowingType.FOLLOWED);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(List.of(followedResponse));

    }

    @Test
    public void 맞팔로우_리스트_조회() throws AccountException {
        // given
        String token = "accessToken";

        User user = buildUserWithIdAndEmail(1L, "user");
        User targetUser = buildUserWithIdAndEmail(2L, "targetUser");

        doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());
        doReturn(user.getEmail()).when(jwtTokenProvider).getUserPk(token);

        Following following = buildFollowingWithIdAndUserFromTo(1L, user, targetUser);
        FollowingResponse followingResponse = FollowingResponse.of(following, FollowingType.FOLLOWING);
        doReturn(List.of(followingResponse)).when(followingRepository).getFollowingToUserListByFromUser(user);

        Following followed = buildFollowingWithIdAndUserFromTo(2L, targetUser, user);
        FollowingResponse followedResponse = FollowingResponse.of(followed, FollowingType.FOLLOWED);
        doReturn(List.of(followedResponse)).when(followingRepository).getFollowingFromUserListByToUser(user);

        // when
        MessageResponse result = followingService.getFollowingUserList(token, FollowingType.BOTH);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(List.of(followingResponse));

    }

    public User buildUserWithIdAndEmail(long id, String email){
        UserDetail userDetail = UserDetail.builder()
                .id(id)
                .nickname(email)
                .build();
        User user = User.builder()
                .id(id)
                .email(email)
                .userDetail(userDetail)
                .build();
        return user;
    }

    public Following buildFollowingWithIdAndUserFromTo(long id, User userFrom, User userTo) {
        return Following.builder()
                .id(id)
                .userFrom(userFrom)
                .userTo(userTo)
                .blockFlag(false)
                .build();
    }

}
