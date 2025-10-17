package com.f1.fastone.common.exception.custom;

import com.f1.fastone.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
