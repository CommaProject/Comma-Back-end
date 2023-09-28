package com.team.comma.domain.user.user.exception;

import com.team.comma.global.constant.ResponseCodeEnum;

public class UserException extends RuntimeException {

    public UserException(ResponseCodeEnum message) {
        super(message.getMessage());
    }
}
