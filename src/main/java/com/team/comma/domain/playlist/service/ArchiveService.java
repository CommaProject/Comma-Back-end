package com.team.comma.domain.playlist.service;

import com.team.comma.domain.playlist.domain.Archive;
import com.team.comma.domain.playlist.repository.archive.ArchiveRepository;
import com.team.comma.domain.playlist.exception.PlaylistException;
import com.team.comma.domain.user.domain.User;
import com.team.comma.domain.user.repository.user.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.domain.playlist.dto.archive.ArchiveRequest;
import com.team.comma.domain.playlist.domain.Playlist;
import com.team.comma.domain.playlist.repository.playlist.PlaylistRepository;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse addArchive(String token , ArchiveRequest archiveRequest) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));
        Playlist playlist = playlistRepository.findById(archiveRequest.getPlaylistId())
                .orElseThrow(() -> new PlaylistException("Playlist를 찾을 수 없습니다."));

        Archive archive = Archive.createArchive(user , archiveRequest.getContent() , playlist);
        archiveRepository.save(archive);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}
