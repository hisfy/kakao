package kr.hisfy.kakao.homework.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionMessages {

    MAX_MAKE_COUNT_EXCESS(HttpStatus.BAD_REQUEST, "발급 한도 초과")
    , COUPON_ISSUE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "쿠폰 발급 실패")
    , COUPON_USE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "쿠폰 사용 실패")
    , COUPON_CANCEL_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "쿠폰 취소 실패")
    , COUPON_EMPTY(HttpStatus.SERVICE_UNAVAILABLE, "쿠폰 없음")
    , TOKEN_GENERATE_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "토큰 생성 실패")
    , TOKE_INVAILD(HttpStatus.FORBIDDEN, "비정상 토큰")
    ;

    private HttpStatus httpStatus;
    private String exceptionMessage;

    ExceptionMessages(HttpStatus httpStatus, String exceptionMessage) {
        this.httpStatus = httpStatus;
        this.exceptionMessage = exceptionMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
