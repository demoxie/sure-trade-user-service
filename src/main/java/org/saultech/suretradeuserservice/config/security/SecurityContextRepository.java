package org.saultech.suretradeuserservice.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.saultech.suretradeuserservice.auth.JwtService;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final List<String> AUTH_WHITELIST = List.of(
            "/login",
            "/register",
            "/verify-otp",
            "/validate",
            "swagger-ui",
            "/telegram/register"
    );
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        if (AUTH_WHITELIST.stream().anyMatch(p -> exchange.getRequest().getPath().value().contains(p))) {
            return Mono.empty();
        }
        String token = extractToken(exchange.getRequest());
       return Mono.just(token)
               .filter(t->t!=null && !t.isEmpty())
               .onErrorResume(e -> Mono.error(() -> APIException.builder()
                       .message(e.getMessage())
                       .statusCode(401)
                       .build()))
               .switchIfEmpty(Mono.error(() -> APIException.builder()
                       .message("No token found in request headers")
                       .statusCode(401)
                       .build()))
                .flatMap(tokens -> authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(tokens, tokens))
                        .map(SecurityContextImpl::new)
                        );
    }

    private String extractToken(ServerHttpRequest request) {
        String tokenList = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (tokenList !=null && !tokenList.isEmpty()) {
            return tokenList.split(" ")[1];
        }
        throw APIException.builder()
                .message("No token found in request headers")
                .statusCode(401)
                .build();
    }
}
