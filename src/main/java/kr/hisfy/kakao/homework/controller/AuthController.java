package kr.hisfy.kakao.homework.controller;

import kr.hisfy.kakao.homework.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

//    @PostMapping(value = "/login" )
    @GetMapping(value = "/login" )
    public Map<String, Object> testAuthController(
            @RequestParam(value = "id") String id
            , @RequestParam(value = "pass") String pass
            , HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        result.put("token", authService.makeToken(id, pass, request));
        return result;
    }
}
