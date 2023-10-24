package zeobase.ZB_technical.challenges.exception;

import lombok.*;
import org.springframework.http.HttpStatus;
import zeobase.ZB_technical.challenges.type.ErrorCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KioskException extends RuntimeException {

    private ErrorCode errorCode;
    private String errorMessage;


    public KioskException(ErrorCode errorCode) {

        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
