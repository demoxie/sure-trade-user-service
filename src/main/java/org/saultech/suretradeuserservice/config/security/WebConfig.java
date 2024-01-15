package org.saultech.suretradeuserservice.config.security;

import io.netty.handler.codec.string.StringEncoder;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v2", c -> true)
                .setUseCaseSensitiveMatch(true);
    }

    @Override
    public void addCorsMappings(org.springframework.web.reactive.config.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Bean
    public OpenAPI customOpenAPI(SwaggerUiConfigProperties swaggerUiConfig) {
        return new OpenAPI()
                .path("/api/v1", new PathItem().description("SureTrade User Service API"))
                .info(
                        new Info()
                                .title("SureTrade User Service API")
                                .version("1.0.0")
                                .description("SureTrade User Service API Documentation")
                )
                .security(
                       List.of(
                                 new io.swagger.v3.oas.models.security.SecurityRequirement()
                                        .addList("bearerAuth")

                       )
                )
                .externalDocs(
                        new io.swagger.v3.oas.models.ExternalDocumentation()
                                .description("SureTrade User Service API Documentation")
                                .url(swaggerUiConfig.getConfigUrl())
                )
                .servers(
                        List.of(
                                new io.swagger.v3.oas.models.servers.Server()
                                        .url("http://localhost:8111/api/v1/")
                                        .description("Local Server")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs()
                .jackson2JsonEncoder(new Jackson2JsonEncoder());
        configurer.defaultCodecs()
                .jackson2JsonDecoder(new org.springframework.http.codec.json.Jackson2JsonDecoder());
        configurer.defaultCodecs().enableLoggingRequestDetails(false);
    }

}
