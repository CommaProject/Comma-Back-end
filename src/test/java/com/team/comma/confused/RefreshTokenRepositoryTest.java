package com.team.comma.confused;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.confused.security.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team.comma.confused.security.RefreshToken;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// EmbeddedDatabase 가 아닌 Mysql 에 테스트 사용됨
public class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository repository;

    private final String refreshToken = "refreshToken";
    private final String keyEmail = "keyEmail";

    @Test
    @DisplayName("refreshToken 탐색")
    public void searchRefreshToken() {
        // given
        RefreshToken refreshTokenInstance = getRefreshToken();

        // when
        repository.save(refreshTokenInstance);
        RefreshToken result = repository.findByToken(refreshTokenInstance.getToken()).get();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("refreshToken 저장")
    public void saveRefreshToken() {
        // given
        RefreshToken refreshTokenInstance = getRefreshToken();

        // when
        RefreshToken result = repository.save(refreshTokenInstance);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("refreshToken 삭제")
    public void removeRefreshToken() {
        // given
        RefreshToken refreshTokenInstance = getRefreshToken();

        // when
        RefreshToken result1 = repository.save(refreshTokenInstance);
        repository.deleteByKeyEmail(refreshTokenInstance.getKeyEmail());
        RefreshToken result2 = repository.existsByKeyEmail(keyEmail);

        // then
        assertThat(result1).isNotNull();
        assertThat(result2).isNull();
        assertThat(result1.getToken()).isEqualTo(refreshToken);
    }

    public RefreshToken getRefreshToken() {
        return RefreshToken.builder()
            .token(refreshToken)
            .keyEmail(keyEmail)
            .build();
    }

}
