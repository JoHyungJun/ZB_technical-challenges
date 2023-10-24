package zeobase.ZB_technical.challenges.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.ErrorCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;


    public MemberException(ErrorCode errorCode) {

        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
