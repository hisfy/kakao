package kr.hisfy.kakao.homework.service;

import kr.hisfy.kakao.homework.client.CallbackClient;
import kr.hisfy.kakao.homework.exception.ApiException;
import kr.hisfy.kakao.homework.exception.ExceptionMessages;
import kr.hisfy.kakao.homework.model.Constants;
import kr.hisfy.kakao.homework.model.CouponModel;
import kr.hisfy.kakao.homework.repository.CouponRepository;
import kr.hisfy.kakao.homework.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponService implements InitializingBean {

    // 다중 서버일 경우 동일한 해쉬값을 방지 하기 위해 인스턴스 기동시 SLAT 값을 세팅한다.
    private static String HASH_SALT_KEY;

    @Autowired
    private CouponRepository repository;

    @Autowired
    private CallbackClient callbackClient;

    /**
     * 쿠폰 발급해준다.
     * @param count
     * @return
     */
    public List<String> makeCoupon(Integer count) {

        // 키생성
        List<String> couponKeys = new ArrayList<>();
        for (int i = 0 ; i < count ; i++) {
            couponKeys.add(Utils.makeCouponKey(i, HASH_SALT_KEY));
        }

        // DB 입력
        LocalDateTime now = LocalDateTime.now();
        List<String> makeCoupon = couponKeys.parallelStream()
                .filter(key -> key != null)
                .map(key -> repository.insertCoupon(key, now))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // hash 충돌이 발생할 경우 재귀 호출 하여 부족한 부분 만큼 재입력한다.
        while (count > makeCoupon.size()) {
            log.info("#makeCoupon 쿠폰 {} 개 부족 재발급 실행", count - makeCoupon.size());
            List<String> additionCoupon = makeCoupon(count - makeCoupon.size());
            makeCoupon.addAll(additionCoupon);
        }

        log.info("#makeCoupon 쿠폰 {} 개 발급 완료", makeCoupon.size());
        return makeCoupon;
    }

    /**
     * 비동기로 쿠폰을 발급해준다.
     * @param count
     * @param url
     * @return
     */
    public LocalDateTime makeCouponAsync(Integer count, String url) {

        LocalDateTime now = LocalDateTime.now();
        CompletableFuture.runAsync(() -> makeAndCallback(count, url));
        return now;
    }

    @Transactional
    public CouponModel issueCoupon(String user) {

        CouponModel coupon = repository.selectUnUseCoupon();

        if (Objects.isNull(coupon)) {
            throw new ApiException(ExceptionMessages.COUPON_EMPTY);
        }

        // 발급 처리
        // 사용기간은 명세가 없어서 일단 7일로 정함
        coupon.issue(user).setEndDate(LocalDateTime.now().plusDays(7));

        boolean isSuccess = repository.updateIssueDate(coupon);

        if (isSuccess) {
            log.info("## 쿠폰 발급 = coupon : {}", coupon);
            return coupon;
        } else {
            throw new ApiException(ExceptionMessages.COUPON_ISSUE_ERROR);
        }
    }

    @Transactional
    public CouponModel useCoupon(String couponKey, String user) {

        CouponModel coupon = repository.selectCouponByCouponKey(couponKey);

        if (coupon.isUsable(user) == false) {
            // 좀더 세밀한 에러?
            throw new ApiException(ExceptionMessages.COUPON_USE_ERROR);
        }

        // 사용처리
        coupon.used(true);

        boolean isSuccess = repository.updateUsed(coupon);
        if (isSuccess) {
            log.info("## 쿠폰 사용처리 = coupon : {}", coupon);
            return coupon;
        } else {
            throw new ApiException(ExceptionMessages.COUPON_USE_ERROR);
        }
    }

    @Transactional
    public CouponModel cancelCoupon(String couponKey, String user) {

        CouponModel coupon = repository.selectCouponByCouponKey(couponKey);

        if (coupon.isCancelable(user) == false) {
            throw new ApiException(ExceptionMessages.COUPON_CANCEL_ERROR);
        }

        // 미사용처리
        coupon.used(false);

        boolean isSuccess = repository.updateUsed(coupon);
        if (isSuccess) {
            log.info("## 쿠폰 사용처리 = coupon : {}", coupon);
            return coupon;
        } else {
            throw new ApiException(ExceptionMessages.COUPON_USE_ERROR);
        }
    }

    public CouponModel statusCoupon(String couponKey) {

        return repository.selectCouponByCouponKey(couponKey);
    }

    /**
     * 비동기 발급 실제 로직
     * @param count
     * @param url
     */
    private void makeAndCallback(Integer count, String url) {

        int nowTotalCount = count;
        int nowCount = Constants.MAX_MAKE_COUNT;
        while( nowTotalCount > 0) {

            if (nowTotalCount < Constants.MAX_MAKE_COUNT) {
                nowTotalCount = 0;
                nowCount = nowTotalCount;
            } else {
                nowTotalCount = nowTotalCount - Constants.MAX_MAKE_COUNT;
            }

            List<String> issuedCoupon = makeCoupon(nowCount);
            log.info("## 비동기 쿠폰 생성 = 요청 개수 : {} / 남은 개수 : {} : 최근 생성 건수 : {} / 진행율 : {}", count, nowTotalCount, nowCount, 100 - (1F * nowTotalCount / count * 100));
            if (StringUtils.isEmpty(url) == false) {
                // 발급된 쿠폰을 callback 해준다. // 정책에 따라 NoSQL 이나 queue 변경한다.
                boolean resultSuccess = callbackClient.sendMadeCouponKey(url, issuedCoupon);
                if (resultSuccess == false) {
                    log.warn("##issueCouponAsync callback fail : couponList : {}", issuedCoupon);
                }
            }

            try {
                Thread.sleep(Constants.COUPON_MASS_INSERT_THREAD_SLEEP);
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        HASH_SALT_KEY = String.valueOf(System.currentTimeMillis() % 1_000 );
        log.warn("### HASH_SALT_KEY SETTING : {}", HASH_SALT_KEY);
    }


}
