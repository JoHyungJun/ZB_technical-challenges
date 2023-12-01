package zeobase.zbtechnical.challenges.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;

/**
 * 공통 사유 관련 CustomException 클래스
 * 
 * @param 
 * @return 
 */
@Getter
public class CommonException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;
    private final String errorMessage;


    public CommonException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
