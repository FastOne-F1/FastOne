package com.f1.fastone.common.exception.custom;

import com.f1.fastone.common.exception.ErrorCode;

public class EntityNotFoundException extends ServiceException {
    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public EntityNotFoundException(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
