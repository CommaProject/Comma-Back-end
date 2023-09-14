package com.team.comma.domain.user.detail.repository;

import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserDetailRepositoryTest {

    @Autowired
    UserDetailRepository userDetailRepository;

    @Test
    public void save() {
        // given
        User user = User.buildUser("userEmail");
        UserDetail userDetail = UserDetail.buildUserDetail(user);

        // when
        userDetailRepository.save(userDetail);

        // then
        Optional<UserDetail> result = userDetailRepository.findById(userDetail.getId());
        assertThat(result.get()).isNotNull();

    }

//    @Test
//    public void findUserDetailByUser() {
//        // given
//        User user = User.buildUser();
//        UserDetail userDetail = UserDetail.buildUserDetail();
//        userDetailRepository.save(userDetail);
//
//        // when
//        Optional<UserDetail> result = userDetailRepository.findUserDetailByUser(user);
//
//        // then
//
//
//    }

}
