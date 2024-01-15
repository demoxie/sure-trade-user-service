//package org.saultech.suretradeuserservice.config.security;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.saultech.suretradeuserservice.auth.JwtService;
//import org.saultech.suretradeuserservice.exception.APIException;
//import org.saultech.suretradeuserservice.user.repository.UserRepository;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class AuthenticationFilter implements WebFilter {
//    private final JwtService jwtService;
//    private final UserRepository userRepository;
//    private static final String AUTH_WHITELIST = "auth";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        //exempt auth endpoints
//        if (exchange.getRequest().getURI().getPath().contains(AUTH_WHITELIST)) {
//            return chain.filter(exchange);
//        }
//        // Extract the token from the request
//        String token = extractToken(exchange.getRequest());
//
//        return jwtService.validateToken(token).flatMap(valid->{
//            if (Boolean.TRUE.equals(valid)) {
//                log.info("Token is valid At Filter");
//                // Token is valid, extract user information
//                String username = jwtService.getUsernameFromToken(token);
//
//                // Check if the user exists in the database
//                return userRepository.findUsersByEmail(username)
//                        .flatMap(user -> {
//                            // Create an authentication object
//                            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                            authentication.setAuthenticated(true);
//
//                            // Set the authentication in the security context
//                            ReactiveSecurityContextHolder.withAuthentication(authentication);
//                            // Continue the filter chain with the authenticated request
//                            chain.filter(exchange);
//                            return Mono.empty();
//                        });
//            } else {
//                // Token is not present or invalid, continue the filter chain
//                log.warn("Warning!: Invalid or missing token");
//                //return unAuthorized exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////                return exchange.getResponse().setComplete();
//                chain.filter(exchange);
//                return Mono.empty();
//            }
//        });
//    }
//
//    // Method to extract the token from the request headers
//    private String extractToken(ServerHttpRequest request) {
//        List<String> authorizationHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
//        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
//            String authorizationHeader = authorizationHeaders.get(0);
//            log.info("Authorization header: {}", authorizationHeader);
//            if (authorizationHeader.startsWith("Bearer ")) {
//                return authorizationHeader.substring(7);
//            }
//        }
//        throw APIException.builder()
//                .statusCode(HttpStatus.UNAUTHORIZED.value())
//                .message("Invalid or missing token")
//                .build();
//    }
//}