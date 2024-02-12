package org.saultech.suretradeuserservice.business.tier.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import reactor.core.publisher.Mono;

public interface TierService {
    Mono<APIResponse> getTiers();

    Mono<APIResponse> getTierForAnAmount(Double amount);

    Mono<APIResponse> getTierById(Long id);
}
