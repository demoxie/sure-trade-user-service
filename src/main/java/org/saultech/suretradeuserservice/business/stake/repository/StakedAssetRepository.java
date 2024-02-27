package org.saultech.suretradeuserservice.business.stake.repository;

import org.saultech.suretradeuserservice.business.stake.entity.StakedAsset;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StakedAssetRepository extends R2dbcRepository<StakedAsset, Long> {
    Mono<StakedAsset> findByUserId(Long userId);

    Mono<StakedAsset> findByUserIdAndCurrency(Long userId, String currency);

    Flux<StakedAsset> findAllByUserIdAndCurrency(long userId, String currency, PageRequest of);
    //Use JOIN between StakedAsset and User table where userId is the foreign key to StakedAsset and walletAddress is the foreign key to User in the below query

    @Query("SELECT * FROM StakedAssets s JOIN Users u ON s.userId = u.id WHERE u.walletAddress = :walletAddress")
    Flux<StakedAsset> findAllByUserWalletAddress(String walletAddress, PageRequest of);

    Flux<StakedAsset> findAllByUserId(long userId, PageRequest of);
}
