package com.team.comma.domain.user.history.service;

import com.team.comma.domain.user.history.dto.HistoryRequest;
import com.team.comma.domain.user.history.dto.HistoryResponse;
import com.team.comma.domain.user.history.repository.HistoryRepository;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.user.exception.UserException;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.common.dto.MessageResponse;
import com.team.comma.global.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.team.comma.global.common.constant.ResponseCodeEnum.NOT_FOUNT_USER;
import static com.team.comma.global.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MessageResponse addHistory(HistoryRequest history , String token) {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        user.addHistory(history.getSearchHistory());

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse getHistoryList(String token) {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        List<HistoryResponse> historyList = historyRepository.getHistoryListByUserEmail(user.getEmail());

        return MessageResponse.of(REQUEST_SUCCESS , historyList);
    }

    @Transactional
    public MessageResponse deleteHistory(long historyId) {
        historyRepository.deleteHistoryById(historyId);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse deleteAllHistory(String token) {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserException(NOT_FOUNT_USER));

        historyRepository.deleteAllHistoryByUser(user);

        return MessageResponse.of(REQUEST_SUCCESS);
    }
}
