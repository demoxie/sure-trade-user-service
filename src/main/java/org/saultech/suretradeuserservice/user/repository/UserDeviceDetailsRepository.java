package org.saultech.suretradeuserservice.user.repository;

import org.saultech.suretradeuserservice.user.entity.UserDeviceDetails;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserDeviceDetailsRepository extends R2dbcRepository<UserDeviceDetails, Long> {
    Mono<UserDeviceDetails> findByUserId(Long userId);

    Mono<UserDeviceDetails> findByDeviceToken(String deviceToken);
}
