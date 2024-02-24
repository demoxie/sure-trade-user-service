package org.saultech.suretradeuserservice.business.tier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.config.app.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierServiceImpl implements TierService{
    private final BusinessConfig appConfig;
    @Override
    public Flux<Tier> getTiers() {
        List<Tier> tierList = getTierList();
        return Flux.fromIterable(tierList);
    }

    @Override
    public Mono<Tier> getTierForAnAmount(Double amount) {
        List<Tier> tierList = getTierList();
        for (Tier tier: tierList) {
            if (amount >= tier.getMinStake() && amount <= tier.getMaxStake()) {
                return Mono.just(tier);
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<Tier> getTierById(Long id) {
        List<Tier> tierList = getTierList();
        for (Tier tier: tierList) {
            if (tier.getId() == id) {
                return Mono.just(tier);
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<Tier> getTierByRange(int min, int max) {
        return getTierList().stream()
                .filter(tier -> tier.getMinStake() == min && tier.getMaxStake() == max)
                .findFirst()
                .map(Mono::just)
                .orElse(Mono.empty());
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
