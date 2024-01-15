package org.saultech.suretradeuserservice.config.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.saultech.suretradeuserservice.auth.JwtService;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        return jwtService.validateToken(authentication.getName())
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(() -> APIException.builder()
                        .message("Invalid token")
                        .statusCode(401)
                        .build()))
                          .map(valid -> {
                              if (Boolean.FALSE.equals(valid)) {
                                  return handleInvalidToken();
                              }
                              Claims claims = jwtService.getAllClaimsFromToken(token);
                                String username = jwtService.getUsernameFromToken(token);
                              List<String> roles = claims.get("role", List.class);
                              return new UsernamePasswordAuthenticationToken(username, null, roles.stream().map(SimpleGrantedAuthority::new).toList());
                          });
    }

    private Authentication handleInvalidToken() {
        throw APIException.builder()
                .message("Invalid token")
                .statusCode(401)
                .build();
    }
}