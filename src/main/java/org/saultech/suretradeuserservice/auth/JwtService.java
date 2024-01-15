package org.saultech.suretradeuserservice.auth;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;

public interface JwtService {
    String generateToken(Map<String,Object> claims, String username);

    Claims getAllClaimsFromToken(String token);

    Date getExpirationDateFromToken(String token);

    String getUsernameFromToken(String token);

    Boolean isTokenExpired(String token);

    Boolean validateToken(String token, UserDetails userDetails);
    Mono<Boolean> validateToken(String token);
}
