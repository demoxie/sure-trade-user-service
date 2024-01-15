package org.saultech.suretradeuserservice.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService{
    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    @Value("${jwt.expiration}")
    private Long expiration;
    @Override
    public String generateToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .signWith(getSigningKey())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 1 week
                .compact();
    }

    private Key getSigningKey() {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    @Override
    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build().parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException | DecodingException e) {
            throw APIException.builder()
                    .message(e.getMessage())
                    .statusCode(401)
                    .build();
        }
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public Boolean isTokenExpired(String token) {
        final Date expectedExpiration = getExpirationDateFromToken(token);
        return expectedExpiration.before(new Date());
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String tokenUsername = getUsernameFromToken(token);
        return tokenUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        final String tokenUsername = getUsernameFromToken(token);
        Mono<User> user = userRepository.findUsersByEmail(tokenUsername);
        return user.flatMap(users->{
            if (Strings.isNotEmpty(users.getToken())) {
                Boolean result = users.getToken().equals(token) && !isTokenExpired(token);
                return Mono.just(result);

            } else {
                return Mono.just(false);
            }
        });
    }
}
