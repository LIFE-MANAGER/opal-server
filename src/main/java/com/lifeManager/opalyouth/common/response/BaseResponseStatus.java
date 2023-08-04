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
    NON_EXIST_USER(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다"),
    LIMIT_NICKNAME_CHANGE(false, HttpStatus.TOO_MANY_REQUESTS.value(), "마지막 닉네임 변경일로부터 7일이 경과하지 않았습니다."),
    EXIST_NICKNAME(false, HttpStatus.CONFLICT.value(), "이미 존재하는 이름입니다."),

    /**
     * Server Error - 5xx 에러
     */
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다.")
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
