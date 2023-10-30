package zeobase.ZB_technical.challenges.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),


    // Token
    EXPIRED_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰입니다."),
    MALFORMED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 형식의 토큰입니다."),
    SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "토큰 서명이 올바르지 않습니다."),
    ILLEGAL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 인자가 전달되었습니다."),


    // Member
    INVALID_SIGN_IN_REQUEST(HttpStatus.BAD_REQUEST, "회원가입 정보로 잘못된 인자가 전달되었습니다."),

    ALREADY_EXISTS_MEMBER_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 ID입니다."),
    ALREADY_EXISTS_PHONE(HttpStatus.BAD_REQUEST, "이미 존재하는 핸드폰 번호입니다."),

    MEMBER_PHONE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 핸드폰 번호입니다."),
    MEMBER_UID_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 아이디입니다."),

    MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    WITHDRAWAL_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),

    // Kiosk


    // Reservation
    RESERVATION_ACCEPTED_REJECTED(HttpStatus.BAD_REQUEST, "점주에 의해 거절된 예약입니다."),
    RESERVATION_ALREADY_CHECKED(HttpStatus.BAD_REQUEST, "이미 방문한 고객입니다."),

    // Review


    // Store


    ;

    private final HttpStatus httpStatus;
    private final String description;
}
