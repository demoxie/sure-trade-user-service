package org.saultech.suretradeuserservice.user.repository;

import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.enums.Role;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findUserByUsername(String username);
    Mono<User> findUsersByEmail(String email);

    Mono<User> findUsersByToken(String token);

    Mono<User> findUsersByOtp(String otp);

    Mono<User> findByRoles(Role role);

    Mono<User> findUsersByWalletAddress(String userWalletAddress);
}
