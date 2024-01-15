package org.saultech.suretradeuserservice.business.repository;

import org.saultech.suretradeuserservice.business.entity.StakedAsset;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StakedAssetRepository extends R2dbcRepository<StakedAsset, Long> {
    Mono<StakedAsset> findByUserId(Long userId);

    Mono<StakedAsset> findByUserIdAndCurrency(Long userId, String currency);

    Flux<StakedAsset> findAllByUserIdAndCurrency(long userId, String currency, PageRequest of);

    Flux<StakedAsset> findAllByUserId(long userId, PageRequest of);
}
