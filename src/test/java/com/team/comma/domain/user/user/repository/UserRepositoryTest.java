package com.team.comma.domain.user.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.domain.user.detail.repository.UserDetailRepository;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.team.comma.domain.user.detail.domain.QUserDetail.userDetail;
import static com.team.comma.domain.user.user.domain.QUser.user;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Test
    @DisplayName("사용자 등록")
    public void save() {
        // given
        User userEntity = User.createUser();

        // when
        User result = userRepository.save(userEntity);

        // then
        assertThat(result.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(result.getPassword()).isEqualTo(userEntity.getPassword());
    }

    @Test
    public void findUserByEmail() {
        // given
        User userEntity = User.createUser();

        // when
        userRepository.save(userEntity);
        Optional<User> result = userRepository.findUserByEmail(userEntity.getEmail());

        // then
        assertThat(result).isNotNull();
        assertThat(result.get().getEmail()).isEqualTo(userEntity.getEmail());
    }

    @Test
    @DisplayName("이름과 닉네임으로 연관된 사용자 탐색")
    public void findUserByNickNameAndName() {
        // given
        User userEntity1 = User.createUser("userEmail1");
        User userEntity2 = User.createUser("userEmail2");
        User userEntity3 = User.createUser("userEmail3");

        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        UserDetail userDetail1 = UserDetail.buildUserDetail(userEntity1);
        UserDetail userDetail2 = UserDetail.buildUserDetail(userEntity2);
        UserDetail userDetail3 = UserDetail.buildUserDetail(userEntity3);

        userDetailRepository.save(userDetail1);
        userDetailRepository.save(userDetail2);
        userDetailRepository.save(userDetail3);

        // when
        List<User> result = userRepository.findAllUsersByNameAndNickName("nickname");

        // then
        assertThat(result.size()).isEqualTo(3);
    }

}
