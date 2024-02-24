package org.saultech.suretradeuserservice.business.tier.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.config.app.Tier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TierService {
    Flux<Tier> getTiers();

    Mono<Tier> getTierForAnAmount(Double amount);

    Mono<Tier> getTierById(Long id);

    Mono<Tier> getTierByRange(int min, int max);

}
