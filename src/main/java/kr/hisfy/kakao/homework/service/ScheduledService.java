package kr.hisfy.kakao.homework.service;

import kr.hisfy.kakao.homework.model.CouponModel;
import kr.hisfy.kakao.homework.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ScheduledService {

    private static final int REMOVE_COUPON_TARGET_DAY = -3;

    @Autowired
    private CouponRepository repository;

    @Scheduled(cron = "00 */5 * * * *")
    public void removeUnusedCoupon() {

        LocalDateTime targetDateTime = LocalDateTime.now().plusDays(REMOVE_COUPON_TARGET_DAY);
        List<CouponModel> user = repository.selectApproachingCoupon(targetDateTime);

        if (CollectionUtils.isEmpty(user)) {
            log.info("## {} 이상 삭제 대상 없음", REMOVE_COUPON_TARGET_DAY);
            return;
        }

        user.stream()
                .map(c -> c.getUser())
                .distinct()
                .collect(Collectors.toList())
                .forEach(userName -> System.out.println(REMOVE_COUPON_TARGET_DAY + " 후에 만료 되는 쿠폰이 있습니다. " + userName));
    }
}
