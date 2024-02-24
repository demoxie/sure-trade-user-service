package org.saultech.suretradeuserservice.business.tier.controller;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.business.tier.service.TierService;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.TIERS;

@RestController
@RequestMapping(value = TIERS, produces = "application/json")
@RequiredArgsConstructor
public class TierController {
    private final TierService tierService;

    @GetMapping("/")
    public Mono<APIResponse> getTiers() {
        return tierService.getTiers()
                .collectList()
                .map(tiers -> APIResponse.builder()
                        .statusCode(200)
                        .message("Tiers retrieved successfully")
                        .data(tiers)
                        .build());
    }

    @GetMapping("/{id}")
    public Mono<APIResponse> getTierById(@PathVariable long id) {
        return tierService.getTierById(id)
                .map(tier -> APIResponse.builder()
                        .statusCode(200)
                        .message("Tier retrieved successfully")
                        .data(tier)
                        .build());
    }

    @GetMapping("/by-range")
    public Mono<APIResponse> getTierByRange(@RequestParam int min, @RequestParam int max) {
        return tierService.getTierByRange(min, max)
                .map(tier -> APIResponse.builder()
                        .statusCode(200)
                        .message("Tier retrieved successfully")
                        .data(tier)
                        .build());
    }

    @GetMapping("/by-amount")
    public Mono<APIResponse> getTierByAmount(@RequestParam double amount) {
        return tierService.getTierForAnAmount(amount)
                .map(tier -> APIResponse.builder()
                        .statusCode(200)
                        .message("Tier retrieved successfully")
                        .data(tier)
                        .build());
    }
}
