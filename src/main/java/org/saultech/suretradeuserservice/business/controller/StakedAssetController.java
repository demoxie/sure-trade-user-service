package org.saultech.suretradeuserservice.business.controller;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.business.dto.StakeAssetDto;
import org.saultech.suretradeuserservice.business.dto.StakeWithdrawalDto;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.business.service.StakedAssetService;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.STAKES;

@RequiredArgsConstructor
@RequestMapping(value = STAKES, produces = "application/json")
@RestController
public class StakedAssetController {
    private final StakedAssetService stakedAssetService;
    @GetMapping("/users/my/stakes")
    public Mono<APIResponse> getMyStakes(@RequestParam String currency) {
        LoggingService.logRequest(currency, "User Service", "/stakes/users/my/stakes", "GET");
        return stakedAssetService.getMyStakes(currency);
    }

    @PostMapping("/")
    public Mono<APIResponse> stakeAsset(@RequestBody StakeAssetDto stakeAssetDto) {
          LoggingService.logRequest(stakeAssetDto, "User Service", "/stakes", "POST");
          return stakedAssetService.stakeAsset(stakeAssetDto);
    }

    @GetMapping("/users/{userId}")
    public Mono<APIResponse> getUserStakes(@PathVariable long userId, @RequestParam(required = false) String currency, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "DESC") String direction) {
        LoggingService.logRequest(userId, "User Service", "/stakes/users/{userId}", "GET");
        return stakedAssetService.getUserStakes(userId, currency, page, size, sort, direction);
    }

    @GetMapping("/users/wallet/{userWalletAddress}")
    public Mono<APIResponse> getUserStakes(@PathVariable String userWalletAddress, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "DESC") String direction) {
        LoggingService.logRequest(userWalletAddress, "User Service", "/stakes/users/wallet/{userWalletAddress}", "GET");
        return stakedAssetService.getUserStakesByWalletAddress(userWalletAddress, page, size, sort, direction);
    }

    @GetMapping("/{stakeId}")
    public Mono<APIResponse> getStake(@PathVariable long stakeId) {
        LoggingService.logRequest(stakeId, "User Service", "/stakes/{stakeId}", "GET");
        return stakedAssetService.getStake(stakeId);
    }

    @PutMapping("/{stakeId}")
    public Mono<APIResponse> updateStake(@PathVariable long stakeId, @RequestBody StakeAssetDto stakeAssetDto) {
        LoggingService.logRequest(stakeAssetDto, "User Service", "/stakes/{stakeId}", "PUT");
        return stakedAssetService.updateStake(stakeId, stakeAssetDto);
    }

    @PostMapping("/request-withdrawal")
    public Mono<APIResponse> requestWithdrawal(@RequestBody StakeWithdrawalDto stakeWithdrawalDto) {
        LoggingService.logRequest(stakeWithdrawalDto, "User Service", "/stakes/request-withdrawal", "GET");
        return stakedAssetService.requestWithdrawal(stakeWithdrawalDto);
    }

    @PutMapping("/request-withdrawal/{stakedId}/cancel")
    public Mono<APIResponse> cancelWithdrawal(@PathVariable long stakedId) {
        LoggingService.logRequest(stakedId, "User Service", "/stakes/request-withdrawal/{stakedId}/cancel", "PUT");
        return stakedAssetService.cancelWithdrawal(stakedId);
    }
}
