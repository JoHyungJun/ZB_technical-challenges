package zeobase.zbtechnical.challenges.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;

/**
 * 매장 관련 CustomException 클래스
 */
@Getter
public class StoreException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;
    private final String errorMessage;


    public StoreException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
