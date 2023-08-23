package com.team.comma.domain.follow.repository;

import com.team.comma.domain.user.following.domain.Following;
import com.team.comma.domain.user.following.dto.FollowingResponse;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.userDetail.UserDetail;
import com.team.comma.domain.user.following.repository.FollowingRepository;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowingRepositoryTest {

    @Autowired
    private FollowingRepository followingRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("나를 팔로우한 사용자 탐색")
    public void searchFollowedMeUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .blockFlag(false)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        User result = followingRepository.getFollowedMeUserByEmail("toEmail" , "fromEmail").orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("toEmail");
    }

    @Test
    @DisplayName("나를 팔로우한 사용자 탐색 없음")
    public void searchFollowedMeUser_none() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .blockFlag(false)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);


        // when
        User result = followingRepository.getFollowedMeUserByEmail("toEmail" , "fromEmails").orElse(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("나를 팔로우한 사람 차단")
    public void blockFollowedUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(false)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        followingRepository.blockFollowedUser("toEmail" , "fromEmail");

        // then
        User result = followingRepository.getBlockedUser("toEmail" , "fromEmail").orElse(null);
        assertThat(result.getEmail()).isEqualTo("toEmail");
    }

    @Test
    @DisplayName("나를 팔로우한 사람 차단 해제")
    public void unblockFollowedUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(true)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        followingRepository.unblockFollowedUser("toEmail" , "fromEmail");

        // then
        User result = followingRepository.getFollowedMeUserByEmail("toEmail" , "fromEmail").orElse(null);
        assertThat(result.getEmail()).isEqualTo("toEmail");
    }

    @Test
    @DisplayName("삭제된 사용자 확인")
    public void isBlockedUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(true)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        User result = followingRepository.getBlockedUser("toEmail" , "fromEmail").orElse(null);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    public void 팔로우_저장 () {
        // given
        User toUser = User.builder()
                .email("toUser1")
                .build();
        User fromUser = User.builder()
                .email("fromUser")
                .build();

        Following follow = Following.createFollowingToFrom(toUser,fromUser);

        // when
        Following result = followingRepository.save(follow);

        // then
        assertThat(result.getUserTo()).isEqualTo(follow.getUserTo());
    }

    @Test
    public void 팔로잉_리스트_조회() {
        // given
        User user = User.builder()
                .email("user")
                .userDetail(UserDetail.builder().nickname("userNickname").build())
                .build();
        User targetUser = User.builder()
                .email("targetUser")
                .userDetail(UserDetail.builder().nickname("targetUserNickname").build())
                .build();

        Following follow = Following.createFollowingToFrom(targetUser, user);
        followingRepository.save(follow);

        // when
        List<FollowingResponse> result = followingRepository.getFollowingToUserListByFromUser(user);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUserId()).isEqualTo(targetUser.getId());
        assertThat(result.get(0).getUserNickname()).isEqualTo(targetUser.getUserDetail().getNickname());

    }

    @Test
    public void 팔로워_리스트_조회() {
        // given
        User user = User.builder()
                .email("user")
                .userDetail(UserDetail.builder().nickname("userNickname").build())
                .build();
        User targetUser = User.builder()
                .email("targetUser")
                .userDetail(UserDetail.builder().nickname("targetUserNickname").build())
                .build();

        Following follow = Following.createFollowingToFrom(user, targetUser);
        followingRepository.save(follow);

        // when
        List<FollowingResponse> result = followingRepository.getFollowingFromUserListByToUser(user);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUserId()).isEqualTo(targetUser.getId());
        assertThat(result.get(0).getUserNickname()).isEqualTo(targetUser.getUserDetail().getNickname());

    }

}
