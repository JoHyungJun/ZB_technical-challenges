package zeobase.zbtechnical.challenges.type.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static zeobase.zbtechnical.challenges.type.common.ErrorMessage.*;

/**
 * 에러 발생 내용을 담은 에러 코드 관련 Enum 클래스
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),

    INVALID_URL_STRING(HttpStatus.BAD_REQUEST, "url 로 잘못된 인자가 전달되었습니다."),


    // Token
    EXPIRED_JWT_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰입니다."),
    MALFORMED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 형식의 토큰입니다."),
    SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "토큰 서명이 올바르지 않습니다."),
    ILLEGAL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 인자가 전달되었습니다."),

    UNAUTHORIZED_RESPONSE(HttpStatus.UNAUTHORIZED, "인증되지 않은 이용자입니다."),


    // Member
    NOT_FOUND_MEMBER_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 멤버 ID 입니다."),
    NOT_FOUND_MEMBER_PHONE(HttpStatus.BAD_REQUEST, "존재하지 않는 핸드폰 번호입니다."),
    NOT_FOUND_MEMBER_UID(HttpStatus.BAD_REQUEST, "이용자 아이디 정보를 찾을 수 없습니다."),

    INVALID_MEMBER_SIGN_IN_REQUEST(HttpStatus.BAD_REQUEST, "회원가입 정보로 잘못된 인자가 전달되었습니다."),
    INVALID_UID_REGEX(HttpStatus.BAD_REQUEST, "잘못된 형식의 아이디입니다."),
    INVALID_NAME_REGEX(HttpStatus.BAD_REQUEST, "잘못된 형식의 이름입니다."),
    INVALID_PASSWORD_REGEX(HttpStatus.BAD_REQUEST, INVALID_PASSWORD_REGEX_MSG),
    INVALID_PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, INVALID_PASSWORD_LENGTH_MSG),
    INVALID_PHONE_REGEX(HttpStatus.BAD_REQUEST, INVALID_PHONE_REGEX_MSG),
    INVALID_MODIFY_REQUEST(HttpStatus.BAD_REQUEST, "이용자 정보 수정 정보로 잘못된 인자가 전달되었습니다."),
    INVALID_MODIFY_ROLE(HttpStatus.BAD_REQUEST, "이용자의 권한을 수정할 수 없습니다."),
    
    ALREADY_EXISTS_MEMBER_UID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ALREADY_EXISTS_PHONE(HttpStatus.BAD_REQUEST, "이미 존재하는 핸드폰 번호입니다."),

    MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    MISMATCH_ROLE(HttpStatus.BAD_REQUEST, "이용자의 권한이 적절하지 않습니다."),

    WITHDRAWAL_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴한 이용자입니다."),
    BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "정지된 이용자입니다."),
    INACTIVE_MEMBER(HttpStatus.BAD_REQUEST, "비활성화된 이용자입니다."),

    EXPIRED_CREDENTIAL(HttpStatus.BAD_REQUEST, "비밀번호가 만료되었습니다."),


    // Kiosk
    INVALID_KIOSK_REQUEST(HttpStatus.BAD_REQUEST, "키오스크 방문 정보로 잘못된 인자가 전달되었습니다."),

    NOT_FOUND_RESERVED_MEMBER(HttpStatus.BAD_REQUEST, "예약된 정보가 없습니다."),


    // Reservation
    NOT_FOUND_RESERVATION_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 예약 ID 입니다."),

    RESERVATION_ACCEPTED_REJECTED(HttpStatus.BAD_REQUEST, "점주에 의해 거절된 예약입니다."),
    RESERVATION_ACCEPTED_WAITING(HttpStatus.BAD_REQUEST, "점주가 아직 승인하지 않은 예약입니다."),
    RESERVATION_CANCELED(HttpStatus.BAD_REQUEST, "이용자가 취소한 예약입니다."),
    
    RESERVATION_ALREADY_CHECKED(HttpStatus.BAD_REQUEST, "이미 방문한 고객입니다."),


    // Review
    NOT_FOUND_REVIEW_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 리뷰 ID 입니다."),
    NOT_FOUND_STORE_VISITED_RECORD(HttpStatus.BAD_REQUEST, "매장을 이용하지 않은 이용자는 리뷰를 남길 수 없습니다."),
    NOT_FOUND_STORE_RESERVED_RECORD(HttpStatus.BAD_REQUEST, "매장을 예약하지 않은 이용자는 리뷰를 남길 수 없습니다."),
    
    INVALID_REVIEW_REQUEST(HttpStatus.BAD_REQUEST, "리뷰 정보로 잘못된 인자가 전달되었습니다."),


    // Store
    NOT_FOUND_STORE_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 가게 ID 입니다."),
    NOT_OWNED_STORE_ID(HttpStatus.BAD_REQUEST, "이용자 소유의 가게가 아닙니다."),

    INVALID_STORE_SIGN_IN_REQUEST(HttpStatus.BAD_REQUEST, "매장 정보로 잘못된 인자가 전달되었습니다."),
    INVALID_SORTED_TYPE(HttpStatus.BAD_REQUEST, "정렬 방식으로 잘못된 인자가 전달되었습니다."),
    INVALID_LOCATION_TYPE(HttpStatus.BAD_REQUEST, "위치 정보로 잘못된 형식의 인자가 전달되었습니다."),
    INVALID_OPENING_HOURS(HttpStatus.BAD_REQUEST, "영업 시간 설정이 올바르지 않습니다."),
    INVALID_RESERVATION_TERM(HttpStatus.BAD_REQUEST, "예약 텀의 시간이 올바르지 않습니다."),
    INVALID_RESERVATION_REQUEST(HttpStatus.BAD_REQUEST, "예약 정보로 잘못된 인자가 전달되었습니다."),
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "예약 시간이 올바르지 않습니다."),

    ALREADY_RESERVED_TIME(HttpStatus.BAD_REQUEST, "이미 예약된 시간입니다."),

    SHUT_DOWN_STORE(HttpStatus.BAD_REQUEST, "영업을 종료한 가게입니다."),
    OPEN_PREPARING_STORE(HttpStatus.BAD_REQUEST, "영업 준비 중인 가게입니다."),
    WITHDRAW_STORE(HttpStatus.BAD_REQUEST, "등록을 해제한 가게입니다."),
    BLOCKED_STORE(HttpStatus.BAD_REQUEST, "정지된 가게입니다."),


    ;

    private HttpStatus httpStatus;
    private String description;


    /**
     * ErrorCode 의 설명을 바꾸기 위한 메서드
     * BindingResult 가 발생했을 시에 활용
     *
     * @param description - BindingResult 에서 전달된 첫 번째 메세지를 담음
     * @return 수정된 자기 자신 ErrorCode
     */
    public ErrorCode modifyDescription(String description) {

        this.description = description;

        return this;
    }
}
