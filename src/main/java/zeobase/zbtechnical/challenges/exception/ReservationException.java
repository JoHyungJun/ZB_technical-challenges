package zeobase.zbtechnical.challenges.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import zeobase.zbtechnical.challenges.type.ErrorCode;

/**
 * 예약 관련 CustomException 클래스
 */
@Getter
public class ReservationException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final String code;
    private final String errorMessage;


    public ReservationException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
