package kr.hisfy.kakao.homework.configration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Slf4j
@Configuration
@PropertySource("application.properties")
public class HikariCpConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String dbClassName;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPass;

    @Lazy
    @Bean
    public DataSource dataSource() {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(dbUser);
        hikariConfig.setPassword(dbPass);
        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setLeakDetectionThreshold(6000);
        hikariConfig.setPoolName("kakakoCoupon");

        final HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }


}