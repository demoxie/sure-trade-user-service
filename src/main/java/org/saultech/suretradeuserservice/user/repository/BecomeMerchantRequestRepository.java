package org.saultech.suretradeuserservice.user.repository;

import org.saultech.suretradeuserservice.user.entity.BecomeMerchantRequests;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BecomeMerchantRequestRepository extends R2dbcRepository<BecomeMerchantRequests, Long> {
    Mono<BecomeMerchantRequests> findByUserId(Long userId);
    Mono<BecomeMerchantRequests> findByEmail(String email);
}
