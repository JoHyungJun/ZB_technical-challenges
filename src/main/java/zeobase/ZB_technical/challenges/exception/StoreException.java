package zeobase.ZB_technical.challenges.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.ErrorCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreException extends RuntimeException {

    private ErrorCode errorCode;
    private String errorMessage;


    public StoreException(ErrorCode errorCode) {

        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
