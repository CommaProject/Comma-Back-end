package com.team.comma.domain.playlist.archive.service;

import com.team.comma.domain.playlist.archive.domain.Archive;
import com.team.comma.domain.playlist.archive.dto.ArchiveRequest;
import com.team.comma.domain.playlist.archive.dto.ArchiveResponse;
import com.team.comma.domain.playlist.archive.repository.ArchiveRepository;
import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.playlist.exception.PlaylistException;
import com.team.comma.domain.playlist.playlist.repository.PlaylistRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse createArchive(final String token , final ArchiveRequest archiveRequest) throws AccountException {
        final String userEmail = jwtTokenProvider.getUserPk(token);
        final User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));
        final Playlist playlist = playlistRepository.findById(archiveRequest.getPlaylistId())
                .orElseThrow(() -> new PlaylistException("Playlist를 찾을 수 없습니다."));

        final Archive archive = Archive.buildArchive(user, archiveRequest.getComment(), playlist);
        archiveRepository.save(archive);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse findArchiveByDate(final String token, LocalDate startDate, LocalDate endDate) throws AccountException {
        final String userEmail = jwtTokenProvider.getUserPk(token);
        final User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        List<ArchiveResponse> archiveResponses = findArchiveByDateTime(
                user, dateToStartDateTime(startDate), dateToEndDateTime(endDate));

        return MessageResponse.of(REQUEST_SUCCESS, archiveResponses);
    }

    public List<ArchiveResponse> findArchiveByDateTime(final User user, final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        return archiveRepository.findArchiveByDateTime(user, startDateTime, endDateTime);
    }

    public LocalDateTime dateToStartDateTime(LocalDate date){
        return date.atStartOfDay();
    }

    public LocalDateTime dateToEndDateTime(LocalDate date){
        return date.atTime(LocalTime.MAX);
    }

}
