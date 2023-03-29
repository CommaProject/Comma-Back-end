package com.team.comma.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team.comma.entity.UserEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // EmbeddedDatabase 가 아닌 Mysql 에 테스트 사용됨
public class userRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;

	private String userEmail = "email@naver.com";
	private String userPassword = "password";
	private String userName = "name";
	
	@Test
	@DisplayName("사용자 등록")
	public void registUser() {
		// given
		UserEntity userEntity = userEntity();
		
		// when
		UserEntity result = userRepository.save(userEntity);
		
		// then
		assertThat(result.getEmail()).isEqualTo(userEmail);
		assertThat(result.getPassword()).isEqualTo(userPassword);
		assertThat(result.getName()).isEqualTo(userName);
	}
	
	@Test
	@DisplayName("사용자 탐색")
	public void findUser() {
		// given
		UserEntity userEntity = userEntity();
		
		// when
		userRepository.save(userEntity);
		UserEntity result = userRepository.findByEmail(userEntity.getEmail());
		
		// then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(userEmail);
	}
	
	private UserEntity userEntity() {
		return UserEntity.builder().email(userEmail).password(userPassword).name(userName).sex("femail").isLeave(0).roles(Collections.singletonList("ROLE_USER"))
				.recommandTime(LocalDateTime.of(2015, 12, 25, 12, 0)).age("20").build();
	}
	
}
