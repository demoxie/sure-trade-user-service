package org.saultech.suretradeuserservice.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ClientSelector {
    @Qualifier("productWebClient")
    private final WebClient productWebClient;
    @Qualifier("paymentWebClient")
    private final WebClient paymentWebClient;

    public WebClient select(String serviceName) {
        switch (serviceName) {
            case "product":
                return productWebClient;
            case "payment":
                return paymentWebClient;
            default:
                return null;
        }
    }
}
