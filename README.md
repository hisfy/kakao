# 카카오페이 쿠폰  API 과제
작성자 : 한치현

## 개발 환경
1. Java 1.8.0_251 
2. SpringBoot 2.1.2 
3. DB 는 개인 서버의 MySQL 로 임시로 open

## 실행
1. boot 실행 : mvn spring-boot:run
2. 빌드 & 실행 : mvn package && java ./target/*.jar

## DB 세팅
1. mysql 의 DB를 생성한후 src/main/resources/application.properties 에 넣는다. 

## 문제 해결 전략
1. 쿠폰 대량 생성시  CouponController.issueCouponAsync 메소드를 이용한다.
2. 대량 쿠폰 생성시 DB 지연이 발생할 경우 Constants.COUPON_MASS_INSERT_THREAD_SLEEP 값을 늘린다.
3. 쿠폰 발생시 리소스 사용을 줄이기 위해 16진수 Hex 값을 바로 2Byte String 으로 변환하였다.
4. 대량 사용이 되면 NoSQL 이나 Queue 를 도입한다.
5. windows 개발이라  sqlmap\\coupon.xml 을 못읽을 경우 MyBatisConfig.class 에서 \\ 를 / 로 변경한다.

## API 설명
테스트 편의상 GET 으로 받을수 있도록 하였다.

### 쿠폰 생성
1. GET : /api/coupon?count=1
1.1. 한건 발급, count 는 default 1
2. GET : /api/coupon/async?count=1&callback=http://a.com/coupon/callback
1.1. 비동기로 발급, 1,000건 단위로 생성 정보를 POST로 callback 해줌 
### 쿠폰 발급
1. GET : /api/coupon?user=abcd
1.1. abcd에게 가장 오래전에 만든 쿠폰 발급 (현재는 7일의 유효기간) / 입력 안 할 경우 누구나 쓸수 있음
### 쿠폰 정보 조회
1. GET  : /api/coupon/{couponKey}/status
### 쿠폰 사용 처리 /  취소
1. GET : /api/coupon/{couponKey}?user=abcd
2. DELETE : /api/coupon/{couponKey}?user=abcd
3. 발급시 user 정보를 안 넣었을 경우 user 정보를 확인하지 않는다.

## 인증 관련
### GET /api/auth/login?id={id}&pass={pass}
1. token 리턴
2. 인증에 대한 정보는 DB가 아닌 클래스 변수에 넣어둠
3. 헤더에 X-AUTH-TOKEN 으로 값을 넣어서 다른 API 를 호출 할 것

## TODO 
1. 인증 관련 부분이 시간 부족으로 싱글톤으로 저장하였음 -> DB로 뺄 것
2. 인증을 interceptor 에 넣었으나 security 에 넣는 것을 고려 한다.
2. 테스트를 위해 API들은 GET으로 대부분 전환 할것
3. Service 만 unit test 하였으나, model 에도 기능을 두어 해당 부분 단위 테스트 추가할 것


