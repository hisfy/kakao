package kr.hisfy.kakao.homework.service;

import kr.hisfy.kakao.homework.model.Constants;
import kr.hisfy.kakao.homework.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class AuthService {

    private Map<String, Long> tokens = new HashMap<>();

    /**
     * 시간 부족으로 날림 코드. 죄송합니다.T_T
     *
     * @param id
     * @param pass
     * @param request
     * @return
     */
    public String makeToken(String id, String pass, HttpServletRequest request) {

        // id / pass 인증을 구현 못함

        // request 에서 slat 를 추가로 뽑으면 좋을텐데,
        long expireTimestamp = System.currentTimeMillis() + (Constants.TOKEN_EXPIRE_TIME * 1_000);
        String expireTimeHex = Utils.getCurrentHexTime(expireTimestamp);

        String token = Utils.makeTokenKey(expireTimeHex, id);

        log.info("## 토큰 생성 = id : {} / token : {}", id, token);
        tokens.put(token, expireTimestamp);
        return token;
    }

    public boolean isLoginByToken(String token) {

        if (Objects.isNull(token)) {
            return false;
        }

        Long expireTime = tokens.get(token);

        if (StringUtils.isEmpty(expireTime)) {
            return false;
        }

        if (System.currentTimeMillis() > expireTime) {
            return false;
        }

        return true;
    }
}
