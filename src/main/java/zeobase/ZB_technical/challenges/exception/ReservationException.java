package zeobase.ZB_technical.challenges.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.ErrorCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;


    public ReservationException(ErrorCode errorCode) {

        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
