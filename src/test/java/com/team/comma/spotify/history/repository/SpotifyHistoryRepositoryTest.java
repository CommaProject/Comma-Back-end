package com.team.comma.spotify.history.repository;

import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SpotifyHistoryRepositoryTest {

    @Autowired
    SpotifyHistoryRepository spotifyHistoryRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("사용자 History 조회")
    public void saveUserHistory() {
        // given
        User user = User.builder().email("email").password("password").role(UserRole.USER).build();
        user.addHistory("History01");
        user.addHistory("History02");
        user.addHistory("History03");
        userRepository.save(user);

        // when
        List<HistoryResponse> result = spotifyHistoryRepository.getHistoryListByUserEmail("email");

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자 History 삭제")
    public void deleteUserHistory() {
        // given
        User user = User.builder().email("email").password("password").role(UserRole.USER).build();
        user.addHistory("History01");
        user.addHistory("History02");
        user.addHistory("History03");
        userRepository.save(user);

        // when
        List<HistoryResponse> resultBefore = spotifyHistoryRepository.getHistoryListByUserEmail("email");
        for(HistoryResponse response : resultBefore) {
            spotifyHistoryRepository.deleteHistoryById(response.getId());
        }
        List<HistoryResponse> resultAfter = spotifyHistoryRepository.getHistoryListByUserEmail("email");

        // when
        assertThat(resultBefore.size()).isEqualTo(3);
        assertThat(resultAfter.size()).isEqualTo(0);
    }
}
