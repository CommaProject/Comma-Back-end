package com.team.comma.follow.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.follow.domain.Following;
import com.team.comma.user.domain.QUser;
import com.team.comma.user.domain.User;
import com.team.comma.util.config.TestConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.stream.Stream;

import static com.team.comma.follow.domain.QFollowing.following;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowingRepositoryTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private FollowingRepository followingRepository;

    @Test
    @DisplayName("나를 팔로우한 사용자 탐색")
    public void searchFollowedMeUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        QUser user1 = new QUser("user1");
        User fromUser = User.builder()
                .email("fromEmail")
                .build();
        QUser user2 = new QUser("user2");

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.email.eq("toEmail"))
                .innerJoin(following.userFrom , user2).on(user2.email.eq("fromEmail"))
                .fetchOne();

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
        QUser user1 = new QUser("user1");
        User fromUser = User.builder()
                .email("fromEmail")
                .build();
        QUser user2 = new QUser("user2");

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);


        // when
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.email.eq("toEmail"))
                .innerJoin(following.userFrom , user2).on(user2.email.eq("fromEmailNone"))
                .fetchOne();

        // then
        assertThat(result).isNull();
    }

    @ParameterizedTest
    @MethodSource("blockUserParameter")
    @DisplayName("나를 팔로우한 사람 차단 및 차단 해제")
    public void blockFollowedUser(final boolean defaultValue , final boolean changeValue) {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        QUser user1 = new QUser("user1");
        User fromUser = User.builder()
                .email("fromEmail")
                .build();
        QUser user2 = new QUser("user2");

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(defaultValue)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        queryFactory.update(following)
                .set(following.blockFlag , changeValue)
                .where(following.id.eq(
                        JPAExpressions.select(following.id).from(following)
                                .innerJoin(following.userTo , user1).on(user1.email.eq("toEmail"))
                                .innerJoin(following.userFrom , user2).on(user2.email.eq("fromEmail"))
                ))
                .execute();

        entityManager.clear();
        // then
        Following result = queryFactory.select(following).from(following)
                .innerJoin(following.userTo , user1).on(user1.email.eq("toEmail"))
                .innerJoin(following.userFrom , user2).on(user2.email.eq("fromEmail"))
                .fetchOne();
        assertThat(result.getBlockFlag()).isEqualTo(changeValue);
    }

    @Test
    @DisplayName("삭제된 사용자 확인")
    public void isBlockedUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        QUser user1 = new QUser("user1");
        User fromUser = User.builder()
                .email("fromEmail")
                .build();
        QUser user2 = new QUser("user2");

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(true)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.email.eq("toEmail"))
                .innerJoin(following.userFrom , user2).on(user2.email.eq("fromEmail"))
                .where(following.blockFlag.eq(true))
                .fetchOne();

        // then
        assertThat(result).isNotNull();
    }

    private static Stream<Arguments> blockUserParameter() {
        return Stream.of(
                Arguments.of(true, false),
                Arguments.of(false, true)
        );
    }

}
