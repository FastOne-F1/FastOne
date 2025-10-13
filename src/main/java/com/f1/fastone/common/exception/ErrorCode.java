package com.f1.fastone.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //user
    USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST.value(), "U001", "이미 가입된 이메일입니다."),
    USER_USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST.value(), "U002", "이미 사용 중인 사용자명입니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "U003", "비밀번호가 일치하지 않습니다."),
    USER_INVALID_MASTER_TOKEN(HttpStatus.BAD_REQUEST.value(), "U004", "관리자 암호가 틀려 등록이 불가능합니다."),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "U005", "권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "U006", "유저를 찾을 수 없습니다."),

    AUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED.value(), "A001", "로그인에 실패했습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "A002", "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "A003", "만료된 토큰입니다."),


    //store
    STORE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "S201", "요청한 가게 카테고리를 찾을 수 없습니다."),
    STORE_CATEGORY_DUPLICATED(HttpStatus.CONFLICT.value(), "S202", "이미 존재하는 가게 카테고리 이름입니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "S001", "스토어를 찾을 수 없습니다."),

    //menu
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "M001", "메뉴를 찾을 수 없습니다."),

    //menu_category
    MENU_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "MC001", "메뉴 카테고리를 찾을 수 없습니다."),

    //order
    ORDER_DETAIL_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "O003", "본인 주문만 접근할 수 있습니다."),
    ORDER_UPDATE_DENIED(HttpStatus.FORBIDDEN.value(), "O004", "주문 상태 변경 권한이 없습니다."),
    ORDER_DELETE_DENIED(HttpStatus.FORBIDDEN.value(), "O005", "음식이 이미 조리 중입니다"),

    // review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "R001", "리뷰를 찾을 수 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "R002", "이미 리뷰가 존재합니다."),
    REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "R003", "본인 리뷰만 접근할 수 있습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "O001", "주문을 찾을 수 없습니다."),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "O002", "본인 주문에만 리뷰를 작성할 수 있습니다."),

    //cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C001", "장바구니를 찾을 수 없습니다."),

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