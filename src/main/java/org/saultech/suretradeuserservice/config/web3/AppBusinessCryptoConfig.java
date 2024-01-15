package org.saultech.suretradeuserservice.config.web3;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.business.crypto")
@Data
public class AppBusinessCryptoConfig {
    private String apikey;
}
