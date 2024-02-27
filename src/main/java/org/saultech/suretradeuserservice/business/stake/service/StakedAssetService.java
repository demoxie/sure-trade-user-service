package org.saultech.suretradeuserservice.business.stake.service;

import org.saultech.suretradeuserservice.business.stake.dto.StakeAssetDto;
import org.saultech.suretradeuserservice.business.stake.dto.StakeWithdrawalDto;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.vo.StakedAssetVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StakedAssetService {
    Flux<StakedAssetVO> getMyStakes(String currency);

    Mono<StakedAssetVO> stakeAsset(StakeAssetDto stakeAssetDto);

    Flux<StakedAssetVO> getUserStakes(long userId, String currency, int page, int size, String sort, String direction);

    Flux<StakedAssetVO> getUserStakesByWalletAddress(String userWalletAddress, int page, int size, String sort, String direction);

    Mono<StakedAssetVO> getStake(long stakeId);

    Mono<APIResponse> updateStake(long stakeId, StakeAssetDto stakeAssetDto);

    Mono<APIResponse> requestWithdrawal(StakeWithdrawalDto stakeWithdrawalDto);

    Mono<APIResponse> cancelWithdrawal(long stakedId);
}
