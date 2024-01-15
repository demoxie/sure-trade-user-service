package org.saultech.suretradeuserservice.config.rabbitmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitmqPropertyConfig {
    private String host;
    private String username;
    private String password;
    private String virtualHost;
    private int port;
    private String uri;
}
