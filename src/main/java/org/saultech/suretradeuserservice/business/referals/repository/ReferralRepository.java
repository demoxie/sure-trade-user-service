package org.saultech.suretradeuserservice.business.referals.repository;

import org.saultech.suretradeuserservice.business.referals.entity.Referral;
import org.saultech.suretradeuserservice.business.referals.enums.ReferralType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReferralRepository extends R2dbcRepository<Referral, Long> {
    Flux<Referral> findAllByReferralCodeAndReferralType(String referralCode, ReferralType referralType);

    Flux<Referral> findAllByReferralCodeAndReferee(String referralCode, Long referee);

    Flux<Referral> findAllByReferrer(Long referrer);

    Flux<Referral> findAllByReferee(Long referee);

    Flux<Referral> findAllByReferralCode(String referralCode);

    Mono<Referral> findAllByReferrerAndReferralType(Long referrer, ReferralType referralType);
}
