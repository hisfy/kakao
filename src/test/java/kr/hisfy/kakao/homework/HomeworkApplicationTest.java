package kr.hisfy.kakao.homework;

import kr.hisfy.kakao.homework.exception.ApiException;
import kr.hisfy.kakao.homework.exception.ExceptionMessages;
import kr.hisfy.kakao.homework.model.CouponModel;
import kr.hisfy.kakao.homework.repository.CouponRepository;
import kr.hisfy.kakao.homework.service.CouponService;
import kr.hisfy.kakao.homework.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.List;

//@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class HomeworkApplicationTest {


    @InjectMocks
    private CouponService service;

    @Mock
    private CouponRepository repository;


    @Test
    public void 쿠폰생성테스트() {

        Mockito.when(repository.insertCoupon(Mockito.anyString(), Mockito.any()))
                .thenReturn("testString");

        List<String> list = service.makeCoupon(10);
        Assert.isTrue( list.size() > 10 , "쿠폰 발급 정상");
    }

    @Test
    public void 쿠폰발급시없을경우에러테스트() {
        Mockito.when(repository.selectUnUseCoupon()).thenReturn(null);
        boolean isError = false;
        try {
            service.issueCoupon("abcd");
        } catch (ApiException e) {
            if (ExceptionMessages.COUPON_EMPTY.getExceptionMessage().equals(e.getErrorMessage())) {
                isError = true;
            }
        }
        Assert.isTrue(isError, "쿠폰 소모시 에러 정상 발생");
    }

    @Test
    public void 쿠폰정상발급테스트() {

        CouponModel couponModel = new CouponModel();
        Mockito.when(repository.selectUnUseCoupon()).thenReturn(couponModel);
        Mockito.when(repository.updateIssueDate(couponModel)).thenReturn(true);

        Assert.isTrue(service.issueCoupon("abcd") == couponModel, "쿠폰 정상 발급");
    }

    @Test
    public void 쿠폰사용시발급한유저가아닐경우에러테스트() {
        CouponModel couponModel = new CouponModel();
        couponModel.setUser("anotherUser");
        Mockito.when(repository.selectUnUseCoupon()).thenReturn(couponModel);

        String couponKey = "1234";
        String user = "user";
        Mockito.when(repository.selectCouponByCouponKey(couponKey)).thenReturn(couponModel);

        boolean isError = false;
        try {
            service.useCoupon(couponKey, user);
        } catch (ApiException e) {
            if (ExceptionMessages.COUPON_USE_ERROR.getExceptionMessage().equals(e.getErrorMessage())) {
                isError = true;
            }
        }

        Assert.isTrue(isError, "유저가 다를경우 정상 에러");
    }

    @Test
    public void 쿠폰사용성공테스트() {
        String couponKey = "1234";
        String user = "user";

        CouponModel couponModel = new CouponModel();
        couponModel.setUser(user);
        Mockito.when(repository.selectCouponByCouponKey(couponKey)).thenReturn(couponModel);
        Mockito.when(repository.updateUsed(couponModel)).thenReturn(true);

        Assert.isTrue(service.useCoupon(couponKey, user) == couponModel, "쿠폰 정상 사용");
    }

    @Test
    public void 쿠폰취소시발급한유저가아닐경우에러테스트() {
        CouponModel couponModel = new CouponModel();
        couponModel.setUser("anotherUser");
        Mockito.when(repository.selectUnUseCoupon()).thenReturn(couponModel);

        String couponKey = "1234";
        String user = "user";
        Mockito.when(repository.selectCouponByCouponKey(couponKey)).thenReturn(couponModel);

        boolean isError = false;
        try {
            service.cancelCoupon(couponKey, user);
        } catch (ApiException e) {
            if (ExceptionMessages.COUPON_USE_ERROR.getExceptionMessage().equals(e.getErrorMessage())) {
                isError = true;
            }
        }

        Assert.isTrue(isError, "유저가 다를경우 정상 에러");
    }

    @Test
    public void  쿠폰취소성공테스트() {
        String couponKey = "1234";
        String user = "user";

        CouponModel couponModel = new CouponModel();
        couponModel.setUser(user);
        Mockito.when(repository.selectCouponByCouponKey(couponKey)).thenReturn(couponModel);
        Mockito.when(repository.updateIssueDate(couponModel)).thenReturn(true);

        Assert.isTrue(service.cancelCoupon(couponKey, user) == couponModel, "쿠폰 취소 성공");
    }

    @Test
    public void utilUnitTest() {
        String hash = Utils.makeCouponKey(1, "100");
        Assert.hasText(hash, "-");
    }

}
