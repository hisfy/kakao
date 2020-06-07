package kr.hisfy.kakao.homework.controller;

import kr.hisfy.kakao.homework.exception.ApiException;
import kr.hisfy.kakao.homework.exception.ExceptionMessages;
import kr.hisfy.kakao.homework.model.Constants;
import kr.hisfy.kakao.homework.model.CouponModel;
import kr.hisfy.kakao.homework.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService service;

    /**
     * 쿠폰 생성
     * @param count
     * @return
     */
    @GetMapping(value = "/make")
    public Map<String, Object> makeCoupon(
            @RequestParam(value = "count", defaultValue = "1", required = false) Integer count) {

        List<String> issuedCouponList = service.makeCoupon(count);

        if (count > Constants.MAX_MAKE_COUNT) {
            throw new ApiException(ExceptionMessages.MAX_MAKE_COUNT_EXCESS);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("couponList", issuedCouponList);
        response.put("couponCount", issuedCouponList.size());

        return response;
    }

    /**
     * 쿠폰 대량 생성 ( 비동기 )
     * @param count
     * @param url
     * @return
     */
    @GetMapping(value = "/make/async")
    public Map<String, Object> makeCouponAsync(
            @RequestParam(value = "count", defaultValue = "1", required = false) Integer count
            , @RequestParam(value = "callback", required = false) String url) {

        LocalDateTime reservedDate = service.makeCouponAsync(count, url);

        Map<String, Object> response = new HashMap<>();
        response.put("couponList", Collections.EMPTY_LIST);
        response.put("couponCount", count);
        response.put("callbackUrl", url);
        response.put("reservedDate", reservedDate);

        return response;
    }

    /**
     * 쿠폰 발급
     * @return
     */
    @GetMapping(value = "")
    public Map<String, Object> issueCoupon(
            @RequestParam(value = "user", required = false) String user) {

        Map<String, Object> response = new HashMap<>();
        response.put("coupon", service.issueCoupon(user));
        response.put("result", "issued");

        return response;
    }

    /**
     * 쿠폰 사용 처리
     * @param couponKey
     * @return
     */
    @GetMapping(value = "/{couponKey}")
    public Map<String, Object> useCoupon(
            @PathVariable(value = "couponKey") String couponKey
            , @RequestParam(value = "user") String user) {

        Map<String, Object> response = new HashMap<>();
        response.put("coupon", service.useCoupon(couponKey, user));
        response.put("result", "used");

        return response;
    }

    /**
     * 쿠폰 정보 조회
     * @param couponKey
     * @return
     */
    @GetMapping(value = "/{couponKey}/status")
    public CouponModel couponInfo(
            @PathVariable(value = "couponKey") String couponKey) {

        return service.statusCoupon(couponKey);
    }

    /**
     * 쿠폰 취소 처리
     * @param couponKey
     * @return
     */
    @DeleteMapping(value = "/{couponKey}")
    public Map<String, Object> cancelCoupon(
            @PathVariable(value = "couponKey") String couponKey
            , @RequestParam(value = "user") String user) {

        Map<String, Object> response = new HashMap<>();
        response.put("coupon", service.cancelCoupon(couponKey, user));
        response.put("result", "cancel");

        return response;
    }
}
