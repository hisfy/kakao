package kr.hisfy.kakao.homework.model;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final int MAX_MAKE_COUNT = 1_000;
    public static final String HASH_ALGORITHM = "SHA-1";

    // HEX 코드라 1에 두자리씩 매핑된다. 즉 두배로 곱한 숫자로 계산된다.
    // ex : xxxxxx-xxxxxx-xxxxxx
    public final static List<Integer> COUPON_DELIMITER_POSITION = Arrays.asList(3, 6);
    public final static int COUPON_KEY_LENGTH = 9; // 18 자리
    public final static int COUPON_MASS_INSERT_THREAD_SLEEP = 50; // ms

    // 인증 관련
    public final static long TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 5; // 초 * 분 * 시 * 일 = 5일
}
