package kr.hisfy.kakao.homework.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class CallbackClient implements InitializingBean {

    private RestTemplate restTemplate;

    private static int readTimeout = 60_000;
    private static int connectionTimeout = 10_000;


    @Override
    public void afterPropertiesSet() throws Exception {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);
        this.restTemplate = new RestTemplate(requestFactory);

        String clientName = this.getClass().getName();
        log.info("{} client setting", clientName);
        log.info("{} client connectionTimeout = {}", clientName, connectionTimeout);
        log.info("{} client readTimeout = {}", clientName, readTimeout);
    }

    public boolean sendMadeCouponKey(String url, List<String> madeCoupon) {

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("madeKey", madeCoupon);
        parameters.add("madeDate", LocalDateTime.now());

//        임시 주석
//        ResponseEntity responseEntity = restTemplate.postForEntity(url, parameters, Object.class);
//        return responseEntity.getStatusCode().equals(HttpStatus.OK);
        log.info("콜백! : URL : {} / list : {}", url, madeCoupon);
        return true;
    }
}
