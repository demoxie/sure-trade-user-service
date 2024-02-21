package org.saultech.suretradeuserservice.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.saultech.suretradeuserservice.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig{
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;
    private final AuthenticationManager authenticationManager;
    private static final String BASE_URL = "/api/v2";
    private static final String[] AUTH_WHITELIST = {
            BASE_URL + "/auth/**",
            BASE_URL + "/webjars/**",
            BASE_URL + "/v3/api-docs/**",
            BASE_URL + "/swagger-ui.html",
            BASE_URL + "/swagger-ui/index.html",
            BASE_URL + "/swagger-ui/",
            BASE_URL + "/socials/**",
    };

    private static final String[] MERCHANT_ROUTES = {
            BASE_URL + "/merchants/**",
    };

    private static final String[] ADMIN_ROUTES = {
            BASE_URL + "/admin/**",
    };

    private static final String[] USER_ROUTES = {
            BASE_URL + "/users/**",
    };

    @Bean
    public SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(
                        ServerHttpSecurity.CsrfSpec::disable
                )
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers(AUTH_WHITELIST).permitAll()
                                .pathMatchers(USER_ROUTES).hasAnyAuthority("USER", "MERCHANT")
                                .pathMatchers(MERCHANT_ROUTES).hasAuthority("MERCHANT")
                                .pathMatchers(ADMIN_ROUTES).hasAuthority("ADMIN")
                                .anyExchange().authenticated()
                )
                .securityContextRepository(securityContextRepository)
                .authenticationManager(authenticationManager)
                .exceptionHandling(
                        exceptionHandlingSpec -> exceptionHandlingSpec
                                .authenticationEntryPoint(
                                        (swe, e) -> Mono.fromRunnable(() -> {
                                            log.error("Unauthorized error: {}", e.getMessage());
                                            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                        })
                                )
                                .accessDeniedHandler(
                                        (swe, e) -> Mono.fromRunnable(() -> {
                                            log.error("Access denied error: {}", e.getMessage());
                                            swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                        })
                                )
                )
                .formLogin(
                        ServerHttpSecurity.FormLoginSpec::disable
                )
                .build();
    }
}
