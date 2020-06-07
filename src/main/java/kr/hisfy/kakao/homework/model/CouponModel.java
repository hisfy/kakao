package kr.hisfy.kakao.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class CouponModel {

    private Long couponSrl;
    private String couponKey;
    private boolean used;
    private LocalDateTime issueDate;
    private LocalDateTime useDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String user;

    public CouponModel(String couponKey, LocalDateTime issueDate, LocalDateTime endDate) {
        this.couponKey = couponKey;
        this.issueDate = issueDate;
        this.endDate = endDate;
    }

    public boolean isUsable(String user) {

        if (used) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(startDate) && now.isBefore(startDate)) {
            return false;
        }

        if (Objects.nonNull(endDate) && now.isAfter(endDate)) {
            return false;
        }

        // 발급한 사람에 표시 되어 있을 경우 사용자랑 맞지 않으면 쓸수 없다.
        if (StringUtils.isEmpty(this.user) == false && this.user.equals(user)  == false) {
            return false;
        }

        return true;
    }

    public boolean isCancelable(String user) {
        // 발급한 사람에 표시 되어 있을 경우 사용자랑 맞지 않으면 쓸수 없다.
        if (StringUtils.isEmpty(this.user) == false && this.user.equals(user)  == false) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (Objects.nonNull(endDate) && now.isAfter(endDate)) {
            return false;
        }

        return true;
    }

    public CouponModel used(boolean used) {
        this.used = used;
        if (this.used) {
            this.useDate = LocalDateTime.now();
        } else {
            this.useDate = null;
        }

        return this;
    }

    public CouponModel issue(String user) {
        this.user = user;
        this.issueDate = LocalDateTime.now();

        return this;
    }
}
