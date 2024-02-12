package org.saultech.suretradeuserservice.payment.repository;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import org.saultech.suretradeuserservice.payment.entity.BankDetails;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BankDetailsRepository extends R2dbcRepository<BankDetails, Long> {
    Mono<BankDetails> findByUserId(Long id);

    Flux<BankDetails> findAllBy();

    Flux<BankDetails> findAllByUserId(long userId);

    Flux<BankDetails> findAllBankDetailsByUserId(long userId);

    Flux<BankDetails> getAllByUserId(Long userId);

    Mono<BankDetails> findAllByAccountNumber(String accountNumber);
}
