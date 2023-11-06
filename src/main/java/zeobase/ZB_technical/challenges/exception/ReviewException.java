package zeobase.ZB_technical.challenges.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import zeobase.ZB_technical.challenges.type.ErrorCode;

/**
 * 리뷰 관련 CustomException 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewException extends RuntimeException {

    private HttpStatus httpStatus;
    private String code;
    private String errorMessage;


    public ReviewException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
