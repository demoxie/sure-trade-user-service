package org.saultech.suretradeuserservice.business.repository;

import org.saultech.suretradeuserservice.business.entity.Tier;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TierRepository extends R2dbcRepository<Tier, Long> {
    Mono<Tier> findTierByTierName(String tierName);
}
