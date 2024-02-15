package org.saultech.suretradeuserservice.business.stake.service;

import org.saultech.suretradeuserservice.business.stake.dto.StakeAssetDto;
import org.saultech.suretradeuserservice.business.stake.dto.StakeWithdrawalDto;
import org.saultech.suretradeuserservice.common.APIResponse;
import reactor.core.publisher.Mono;

public interface StakedAssetService {
    Mono<APIResponse> getMyStakes(String currency);

    Mono<APIResponse> stakeAsset(StakeAssetDto stakeAssetDto);

    Mono<APIResponse> getUserStakes(long userId, String currency, int page, int size, String sort, String direction);

    Mono<APIResponse> getUserStakesByWalletAddress(String userWalletAddress, int page, int size, String sort, String direction);

    Mono<APIResponse> getStake(long stakeId);

    Mono<APIResponse> updateStake(long stakeId, StakeAssetDto stakeAssetDto);

    Mono<APIResponse> requestWithdrawal(StakeWithdrawalDto stakeWithdrawalDto);

    Mono<APIResponse> cancelWithdrawal(long stakedId);
}
