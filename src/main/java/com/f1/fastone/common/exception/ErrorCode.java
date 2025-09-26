package com.f1.fastone.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //user

    //store

    //menu

    //order

    //review

    //cart

    //common
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "C001", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "C002", "입력값이 올바르지 않습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST.value(), "C003", "요청 데이터 타입이 올바르지 않습니다."),

    ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "C004", "해당 리소스에 접근할 권한이 없습니다."),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C005", "엔티티를 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C006", "리소스를 찾을 수 없습니다."),

    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "C007", "허용되지 않은 요청 메서드입니다."),

    EXTERNAL_SERVER_ERROR(HttpStatus.BAD_GATEWAY.value(), "C998", "외부 서비스 연동 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "C999", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}