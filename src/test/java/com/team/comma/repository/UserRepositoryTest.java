package com.team.comma.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserEntity.UserType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private String userEmail = "email@naver.com";
	private String userName = "userName";

	@Test
	@DisplayName("OAuth 사용자 저장")
	public void loginOauth() {
		// given
		UserEntity userEntity = getuserEntity();

		// when
		UserEntity result = userRepository.save(userEntity);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getPassword()).isNull();
		assertThat(result.getEmail()).isEqualTo(userEmail);
	}

	@Test
	@DisplayName("OAuth 사용자 조회")
	public void getOauth() {
		// given
		UserEntity userEntity = getuserEntity();
		userRepository.save(userEntity);

		// when
		UserEntity result = userRepository.findByEmail(userEntity.getEmail());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getPassword()).isNull();
		assertThat(result.getEmail()).isEqualTo(userEmail);
		assertThat(result.getUserType()).isEqualTo(UserType.OAuthUser);
	}

	public UserEntity getuserEntity() {
		return UserEntity.builder().age("20").email(userEmail).isLeave(0).name(userName).userType(UserType.OAuthUser)
				.recommandTime(LocalDateTime.of(2015, 12, 25, 12, 0)).password(null).sex("femail").build();
	}

}
