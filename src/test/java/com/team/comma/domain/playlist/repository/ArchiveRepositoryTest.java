package com.team.comma.domain.playlist.repository;

import com.team.comma.domain.playlist.archive.domain.Archive;
import com.team.comma.domain.playlist.archive.repository.ArchiveRepository;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArchiveRepositoryTest {

    @Autowired
    ArchiveRepository archiveRepository;

    @Test
    @DisplayName("아카이브 정보 저장")
    public void saveArchive() {
        // given
        User user = User.builder().build();
        Playlist playlist = Playlist.builder().build();
        Archive archive = Archive.createArchive(user , "content" , playlist);

        // when
        Archive result = archiveRepository.save(archive);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("content");
    }
}
