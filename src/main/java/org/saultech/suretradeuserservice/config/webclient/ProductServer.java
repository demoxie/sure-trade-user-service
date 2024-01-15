package org.saultech.suretradeuserservice.config.webclient;

import lombok.Data;

@Data
public class ProductServer {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String[] scopes;
}
