package org.saultech.suretradeuserservice.config.webclient;

import lombok.Data;

@Data
public class PaymentServer {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String[] scopes;
}
