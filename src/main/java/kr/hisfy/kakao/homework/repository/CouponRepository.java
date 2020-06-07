package kr.hisfy.kakao.homework.repository;

import kr.hisfy.kakao.homework.model.CouponModel;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CouponRepository extends SqlSessionDaoSupport {

    protected static final String NAMESPACE = "kr.hisfy.kakao.homework.repository.CouponRepository.";

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public String insertCoupon(String key, LocalDateTime now) {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("couponKey", key);
        parameter.put("createDate", now.format(df));
        SqlSession sqlSession = getSqlSession();
        return sqlSession.insert(NAMESPACE + "insertCoupon", parameter) > 0 ? key : null;
    }

    public CouponModel selectUnUseCoupon() {

        SqlSession sqlSession = getSqlSession();
        return sqlSession.selectOne(NAMESPACE + "selectUnusedCoupon");
    }

    public boolean updateIssueDate(CouponModel coupon) {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("couponSrl", coupon.getCouponSrl());
        parameter.put("issueDate", coupon.getIssueDate().format(df));

        SqlSession sqlSession = getSqlSession();
        return sqlSession.update(NAMESPACE + "updateIssueDate", parameter) > 0;
    }

    public boolean updateUsed(CouponModel coupon) {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("couponSrl", coupon.getCouponSrl());
        parameter.put("useDate", coupon.getUseDate());

        SqlSession sqlSession = getSqlSession();
        return sqlSession.update(NAMESPACE + "updateUsed", parameter) > 0;
    }

    public CouponModel selectCouponByCouponKey(String couponKey) {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("couponKey", couponKey);

        SqlSession sqlSession = getSqlSession();
        return sqlSession.selectOne(NAMESPACE + "selectCouponByCouponKey", parameter);
    }

    public List<CouponModel> selectApproachingCoupon(LocalDateTime targetDateTime) {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("endDate", targetDateTime);
        parameter.put("used", false);

        SqlSession sqlSession = getSqlSession();
        return sqlSession.selectList(NAMESPACE + "selectApproachingCoupon", parameter);
    }

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Autowired
    @Override
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }
}
