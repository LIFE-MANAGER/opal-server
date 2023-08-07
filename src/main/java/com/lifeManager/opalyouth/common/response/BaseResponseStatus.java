package com.lifeManager.opalyouth.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {
    /**
     * 성공 코드 2xx
     * 코드의 원활한 이해을 위해 code는 숫자가 아닌 아래 형태로 입력해주세요.
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * Client Error - 4xx 에러
     */
    NO_AUTH(false, HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다."),
    EXIST_EMAIL(false, HttpStatus.CONFLICT.value(), "이미 존재하는 회원입니다"),
    INACTIVE_ACCOUNT(false, HttpStatus.BAD_REQUEST.value(), "휴면 계정입니다."),
    NON_EXIST_USER(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다"),
    WRONG_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "잘못된 비밀번호 입니다."),
    EXPIRED_JWT_TOKEN(false, HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다"),
    INVALID_JWT_TOKEN(false, HttpStatus.NOT_ACCEPTABLE.value(), "유효하지 않은 토큰입니다."),


    /**
     * Server Error - 5xx 에러
     */
    IMAGE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 저장에 실패하였습니다."),
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 에러가 발생하였습니다."),
    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    /**
     * isSuccess : 요청의 성공 또는 실패
     * code : Http Status Code
     * message : 설명
     */
    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
