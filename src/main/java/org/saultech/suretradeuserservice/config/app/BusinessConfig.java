package org.saultech.suretradeuserservice.config.app;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.business")
@Data
public class BusinessConfig {
    private Business business;
}
