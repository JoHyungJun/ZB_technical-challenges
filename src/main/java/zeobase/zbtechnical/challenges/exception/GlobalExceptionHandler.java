package zeobase.zbtechnical.challenges.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zeobase.zbtechnical.challenges.dto.common.ErrorResponse;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;

/**
 * 전체 Exception 을 핸들링하는 ExceptionHandler 클래스
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ErrorResponse handleJwtException(JwtException e) {

        log.error("{} is occurred", e.getCode());

        return new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(KioskException.class)
    public ErrorResponse handleKioskException(KioskException e) {

        log.error("{} is occurred", e.getCode());

        return new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MemberException.class)
    public ErrorResponse handleMemberException(MemberException e) {

        log.error("{} is occurred", e.getCode());

        return new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(ReservationException.class)
    public ErrorResponse handleReservationException(ReservationException e) {

        log.error("{} is occurred", e.getCode());

        return new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(ReviewException.class)
    public ErrorResponse handleReviewException(ReviewException e) {

        log.error("{} is occurred", e.getCode());

        return new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(StoreException.class)
    public ErrorResponse handleStoreException(StoreException e) {

        log.error("{} is occurred", e.getCode());

        return new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(CommonException.class)
    public ErrorResponse handleCommonException(CommonException e) {

        log.error("{} is occurred", e.getCode());

        return new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {

        log.error("{} is occurred", e);

        return new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                e.getMessage());
    }
}
