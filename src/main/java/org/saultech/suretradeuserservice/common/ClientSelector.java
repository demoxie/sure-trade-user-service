package org.saultech.suretradeuserservice.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ClientSelector {
    @Qualifier("productWebClient")
    private final WebClient productWebClient;
    @Qualifier("paymentWebClient")
    private final WebClient paymentWebClient;

    public ClientSelector(WebClient productWebClient, WebClient paymentWebClient) {
        this.productWebClient = productWebClient;
        this.paymentWebClient = paymentWebClient;
    }

    public WebClient select(String serviceName) {
        return switch (serviceName) {
            case "product" -> productWebClient;
            case "payment" -> paymentWebClient;
            default -> null;
        };
    }
}
