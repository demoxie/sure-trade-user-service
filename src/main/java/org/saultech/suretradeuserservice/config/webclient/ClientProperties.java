package org.saultech.suretradeuserservice.config.webclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "client")
public class ClientProperties {
    private ProductServer productServer;
    private PaymentServer paymentServer;
    private MessagingServer messagingServer;
    private Integer timeout;
}
