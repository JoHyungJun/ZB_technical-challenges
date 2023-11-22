package zeobase.zbtechnical.challenges.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import zeobase.zbtechnical.challenges.type.ErrorCode;

/**
 * 공통 에러 응답 response 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;


    public ErrorResponse(ErrorCode errorCode) {

        this.httpStatus = errorCode.getHttpStatus();
        this.errorCode = errorCode.name();
        this.errorMessage = errorCode.getDescription();
    }
}
