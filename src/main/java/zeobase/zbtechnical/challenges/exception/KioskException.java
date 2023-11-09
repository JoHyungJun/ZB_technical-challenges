package zeobase.zbtechnical.challenges.exception;

import lombok.*;
import org.springframework.http.HttpStatus;
import zeobase.zbtechnical.challenges.type.ErrorCode;

/**
 * 키오스크 관련 CustomException 클래스
 */
@Getter
public class KioskException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;
    private final String errorMessage;


    public KioskException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
