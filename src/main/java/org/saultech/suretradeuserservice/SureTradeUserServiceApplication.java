package org.saultech.suretradeuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication(scanBasePackages = {"org.saultech.suretradeuserservice", "com.hanqunfeng.reactive.redis.cache"})
@EnableWebFlux
@EnableR2dbcRepositories
@EnableR2dbcAuditing
@EnableRetry
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class SureTradeUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SureTradeUserServiceApplication.class, args);
    }

}
