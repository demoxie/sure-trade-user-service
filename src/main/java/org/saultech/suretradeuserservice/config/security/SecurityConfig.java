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
    private static final String[] AUTH_WHITELIST = {
            "/api/v2/permissions/**",
            "/api/v2/swagger-ui/**",
            "/api/v2/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/v2/swagger-resources/**",
            "/api/v2/api-docs/**",
            "/api-docs/**",
            "/api/v2/actuator/**",
            "/api/v2/h2-console/**",
            "/api/v2/",
            "/",
            "/webjars/swagger-ui/index.html",
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
                                .pathMatchers("/api/v2/auth/**").permitAll()
                                .pathMatchers("/api/v2/users/**").hasAnyAuthority("USER", "ADMIN", "MERCHANT")
                                .pathMatchers("/webjars/**", "/swagger-ui.html", "/v3/api-docs/**")
                                .permitAll()
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
