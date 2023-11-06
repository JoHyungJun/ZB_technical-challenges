package zeobase.ZB_technical.challenges.exception;

import lombok.*;
import org.springframework.http.HttpStatus;
import zeobase.ZB_technical.challenges.type.ErrorCode;

/**
 * 키오스크 관련 CustomException 클래스
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KioskException extends RuntimeException {

    private HttpStatus httpStatus;
    private String code;
    private String errorMessage;


    public KioskException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
