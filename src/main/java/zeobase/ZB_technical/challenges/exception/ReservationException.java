package zeobase.ZB_technical.challenges.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import zeobase.ZB_technical.challenges.type.ErrorCode;

/**
 * 예약 관련 CustomException 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationException extends RuntimeException{

    private HttpStatus httpStatus;
    private String code;
    private String errorMessage;


    public ReservationException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
