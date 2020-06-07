package kr.hisfy.kakao.homework.util;

import kr.hisfy.kakao.homework.exception.ApiException;
import kr.hisfy.kakao.homework.exception.ExceptionMessages;
import kr.hisfy.kakao.homework.model.Constants;
import lombok.extern.slf4j.Slf4j;

import java.rmi.server.ExportException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Utils {

    /**
     * SHA-1 해쉬로 쿠폰을 발급한다.
     * 효율을 위해, byte[] 에서 바로 쿠폰키를 생성한다.
     * @param idx
     * @return
     */
    public static String makeCouponKey(Integer idx, String systemHashSalt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(Constants.HASH_ALGORITHM);
            digest.update((systemHashSalt + idx.toString() + System.currentTimeMillis()).getBytes());
            byte[] bytes = digest.digest();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < bytes.length ; i++ ) {
                if (i >= Constants.COUPON_KEY_LENGTH) {
                    break;
                }
                if (Constants.COUPON_DELIMITER_POSITION.contains(i)) {
                    builder.append("-");
                }
                builder.append(String.format("%02x", bytes[i]));
            }
            return builder.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("#makeCouponKey : NoSuchAlgorithmException", e);
        }

        return null;
    }

    public static String makeTokenKey(String... keys) {

        StringBuilder sb = new StringBuilder();

        for (String s : keys) {
            sb.append(s);
            sb.append("+");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(Constants.HASH_ALGORITHM);
            digest.update(sb.toString().getBytes());

            StringBuilder sb2 = new StringBuilder();

            for (byte b : digest.digest()) {
                sb2.append(String.format("%02x", b));
            }

            return sb2.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new ApiException(ExceptionMessages.TOKEN_GENERATE_FAIL);
        }
    }


    public static String getCurrentHexTime(Long currentMillsTime) {

        String hexTime = Long.toHexString(currentMillsTime);
        log.info("##getCurrentHexTime : inTime = {}, hexTIme = {}", currentMillsTime, hexTime);

        return hexTime;
    }

    public static Long hexTimeToTimestamp(String hexTime) {

        Long timeStamp = Long.parseLong(hexTime, 16);
        log.info("##hexTimeToTimestamp : hexTime = {}, timeStamp = {}", hexTime, timeStamp);

        return timeStamp;
    }
}
