package com.f1.fastone.common.exception;

import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //user
    USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST.value(), "U001", "이미 가입된 이메일입니다."),
    USER_USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST.value(), "U002", "이미 사용 중인 사용자명입니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "U003", "비밀번호가 일치하지 않습니다."),
    USER_INVALID_ADMIN_TOKEN(HttpStatus.BAD_REQUEST.value(), "U004", "관리자 암호가 틀려 등록이 불가능합니다."),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "U005", "권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "U006", "유저를 찾을 수 없습니다."),

    AUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED.value(), "A001", "로그인에 실패했습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "A002", "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "A003", "만료된 토큰입니다."),


    //store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "S001", "가게를 찾을 수 없습니다."),

    //menu
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "M001", "메뉴를 찾을 수 없습니다."),

    //menu_category
    MENU_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "MC001", "메뉴 카테고리를 찾을 수 없습니다."),

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