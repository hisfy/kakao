package kr.hisfy.kakao.homework.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private HttpStatus httpStatus;
    private String errorMessage;

    public ApiException() {
        super();
    }

    public ApiException(ExceptionMessages e) {
        this.httpStatus = e.getHttpStatus();
        this.errorMessage = e.getExceptionMessage();
    }

    public ApiException(ExceptionMessages e, Throwable t) {
        super(e.getExceptionMessage(), t);
        this.httpStatus = e.getHttpStatus();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
