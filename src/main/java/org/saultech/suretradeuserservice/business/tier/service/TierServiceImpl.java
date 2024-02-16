package org.saultech.suretradeuserservice.business.tier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.config.app.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierServiceImpl implements TierService{
    private final BusinessConfig appConfig;
    @Override
    public Mono<APIResponse> getTiers() {
        List<Tier> tierList = getTierList();
        return Mono.just(
                APIResponse.builder()
                        .data(tierList)
                        .message("Tiers fetched successfully")
                        .statusCode(200)
                        .build()
        );
    }

    @Override
    public Mono<APIResponse> getTierForAnAmount(Double amount) {
        List<Tier> tierList = getTierList();
        for (Tier tier: tierList) {
            if (amount >= tier.getMinStake() && amount <= tier.getMaxStake()) {
                return Mono.just(
                        APIResponse.builder()
                                .data(tier)
                                .message("Tier fetched successfully")
                                .statusCode(200)
                                .build()
                );
            }
        }
        return Mono.just(
                APIResponse.builder()
                        .message("Tier not found")
                        .statusCode(404)
                        .build()
        );
    }

    @Override
    public Mono<APIResponse> getTierById(Long id) {
        List<Tier> tierList = getTierList();
        for (Tier tier: tierList) {
            if (tier.getId() == id) {
                return Mono.just(
                        APIResponse.builder()
                                .data(tier)
                                .message("Tier fetched successfully")
                                .statusCode(200)
                                .build()
                );
            }
        }
        return Mono.just(
                APIResponse.builder()
                        .message("Tier not found")
                        .statusCode(404)
                        .build()
        );
    }

    @NotNull
    private List<Tier> getTierList() {
        Business business = appConfig.getBusiness();
        Tiers tiers = business.getTiers();
        Tier tier1 = tiers.getTier1();
        Tier tier2 = tiers.getTier2();
        Tier tier3 = tiers.getTier3();
        Tier tier4 = tiers.getTier4();
        Tier tier5 = tiers.getTier5();
        Tier tier6 = tiers.getTier6();
        return List.of(tier1, tier2, tier3, tier4, tier5, tier6);
    }
}
