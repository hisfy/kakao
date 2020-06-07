package kr.hisfy.kakao.homework.interceptor;

import kr.hisfy.kakao.homework.exception.ApiException;
import kr.hisfy.kakao.homework.exception.ExceptionMessages;
import kr.hisfy.kakao.homework.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = request.getHeader("X_AUTH_TOKEN");
        if (authService.isLoginByToken(token)) {
            return true;
        }

        throw new ApiException(ExceptionMessages.TOKE_INVAILD);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        return;
    }
}