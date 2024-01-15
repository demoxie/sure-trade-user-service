package org.saultech.suretradeuserservice.config.web3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CryptoConfig {
    private final AppBusinessCryptoConfig appBusinessCryptoConfig;

    @Bean
    public Web3j web3j() {
        return Web3j
                .build(new HttpService("https://rinkeby.infura.io/v3/" + appBusinessCryptoConfig.getApikey()));
    }

}
