package org.saultech.suretradeuserservice.config.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final ClientProperties clientProperties;


    @Bean(name = "productWebClient")
    public WebClient productWebClient() {

        return WebClient
                .builder()
                .baseUrl(clientProperties.getProductServer().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .defaultHeaders(httpHeaders -> httpHeaders
                        .setAll(
                                java.util.Map.of(
                                        "X-API-KEY", clientProperties.getProductServer().getClientId(),
                                        "X-API-SECRET", clientProperties.getProductServer().getClientSecret(),
                                        "Content-Type", MediaType.APPLICATION_JSON_VALUE,
                                        "Accept", MediaType.APPLICATION_JSON_VALUE
                                )
                        )
                )
                .build();
    }

    @Bean(name = "paymentWebClient")
    public WebClient paymentWebClient(){
        return WebClient.builder()
                .baseUrl(clientProperties.getPaymentServer().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .defaultHeaders(
                        httpHeaders -> httpHeaders
                                .setAll(
                                        Map.of(
                                                "X-API-KEY", clientProperties.getPaymentServer().getClientId(),
                                                "X-API-SECRET", clientProperties.getPaymentServer().getClientSecret(),
                                                "Content-Type", MediaType.APPLICATION_JSON_VALUE,
                                                "Accept", MediaType.APPLICATION_JSON_VALUE
                                        )
                                )
                )
                .build();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProperties.getTimeout())
                .responseTimeout(Duration.ofMillis(clientProperties.getTimeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(clientProperties.getTimeout(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(clientProperties.getTimeout(), TimeUnit.MILLISECONDS)));
    }
}
