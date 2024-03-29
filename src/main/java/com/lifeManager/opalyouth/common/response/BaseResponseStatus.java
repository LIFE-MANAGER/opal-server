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
    MATCH(true, HttpStatus.ACCEPTED.value(), "친구 목록에 추가되었습니다."),

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
    LIMIT_NICKNAME_CHANGE(false, HttpStatus.TOO_MANY_REQUESTS.value(), "마지막 닉네임 변경일로부터 7일이 경과하지 않았습니다."),
    EXIST_NICKNAME(false, HttpStatus.CONFLICT.value(), "이미 존재하는 이름입니다."),
    ALREADY_BLOCKED(false, HttpStatus.CONFLICT.value(), "이미 차단한 회원입니다."),
    IMAGE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 이미지입니다."),
    ALREADY_REQUESTED(false, HttpStatus.CONFLICT.value(), "이미 친구 요청을 보냈습니다."),
    EXIST_CHAT_ROOM(false, HttpStatus.CONFLICT.value(), "이미 존재하는 채팅방입니다."),
    NON_EXIST_CHAT_ROOM(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 채팅방입니다."),
    NO_DIAMONDS(false, HttpStatus.BAD_REQUEST.value(), "다이아가 부족합니다."),
    NO_RECOMMENDED_FRIENDS(false, HttpStatus.NO_CONTENT.value(), "취향에 맞는 친구가 없습니다."),

    /**
     * Server Error - 5xx 에러
     */
    IMAGE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 저장에 실패하였습니다."),
    SEND_MESSAGE_FAILURE(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "인증 코드 전송에 실패하였습니다."),
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다."),
    INIT_TODAY_FRIENDS_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "오늘의 친구 추천에 실패하였습니다."),
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
