package zeobase.ZB_technical.challenges.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),

    // Member
    MEMBER_PHONE_NOT_FOUND("존재하지 않는 핸드폰 번호입니다."),

    MEMBER_WITHDRAW("탈퇴한 회원입니다."),

    // Kiosk


    // Reservation
    RESERVATION_ACCEPTED_REJECTED("점주에 의해 거절된 예약입니다."),
    RESERVATION_ALREADY_CHECKED("이미 방문한 고객입니다."),

    // Review


    // Store


    ;

    private final String description;
}
