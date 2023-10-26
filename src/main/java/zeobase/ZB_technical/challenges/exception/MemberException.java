package zeobase.ZB_technical.challenges.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import zeobase.ZB_technical.challenges.type.ErrorCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberException extends RuntimeException{

    private HttpStatus httpStatus;
    private String code;
    private String errorMessage;


    public MemberException(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
