package org.saultech.suretradeuserservice.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.business.dto.StakeAssetDto;
import org.saultech.suretradeuserservice.business.dto.StakeWithdrawalDto;
import org.saultech.suretradeuserservice.business.entity.StakedAsset;
import org.saultech.suretradeuserservice.business.repository.StakedAssetRepository;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.products.giftcard.vo.StakedAssetVO;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class StakedAssetServiceImpl implements StakedAssetService {
    private final StakedAssetRepository stakedAssetRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    @Override
    public Mono<APIResponse> getMyStakes(String currency) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> stakedAssetRepository.findByUserIdAndCurrency(user.getId(), currency))
                .map(stakedAsset -> {
                    StakedAssetVO stakedAssetVO = mapper.map(stakedAsset, StakedAssetVO.class);
                    stakedAssetVO.setCreatedAt(stakedAsset.getCreatedAt());
                    stakedAssetVO.setUpdatedAt(stakedAsset.getUpdatedAt());
                    return APIResponse.builder()
                            .message("Staked asset retrieved successfully")
                            .statusCode(200)
                            .data(stakedAssetVO)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> stakeAsset(StakeAssetDto stakeAssetDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> stakedAssetRepository.findByUserIdAndCurrency(user.getId(), stakeAssetDto.getCurrency())
                        .switchIfEmpty(
                                Mono.defer(()->{
                                    StakedAsset stakedAsset = mapper.map(stakeAssetDto, StakedAsset.class);
                                    stakedAsset.setUserId(user.getId());
                                    return Mono.just(stakedAsset);
                                })
                        ))
                .flatMap(stakedAsset -> {
                    stakedAsset.setPreviousBalance(stakedAsset.getBalance() == null ? BigDecimal.valueOf(0) : stakedAsset.getBalance());
                    stakedAsset.setBalance(
                            stakedAsset.getBalance() == null ?
                                    stakeAssetDto.getAmount() :
                                    stakeAssetDto.getAmount().add(stakedAsset.getBalance())
                    );
                    stakedAsset.setStatus("UNCONFIRMED");
                    stakedAsset.setUpdatedAt(LocalDateTime.now());
                    stakedAsset.setCreatedAt(LocalDateTime.now());
                    return stakedAssetRepository.save(stakedAsset)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .statusCode(500)
                                            .message("Error staking asset")
                                            .build()
                            ));
                })
                .map(stakedAsset -> {
                    StakedAssetVO stakedAssetVO = mapper.map(stakedAsset, StakedAssetVO.class);
                    stakedAssetVO.setCreatedAt(stakedAsset.getCreatedAt());
                    stakedAssetVO.setUpdatedAt(stakedAsset.getUpdatedAt());
                    return APIResponse.builder()
                            .message("Asset staked successfully")
                            .statusCode(200)
                            .data(stakedAssetVO)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> getUserStakes(long userId, String currency, int page, int size, String sort, String direction) {
        Sort sort1 = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sort);
        if (currency == null)
            return stakedAssetRepository.findAllByUserId(userId, PageRequest.of((page-1), size, sort1))
                    .flatMap(stakedAsset -> {
                        StakedAssetVO stakedAssetVO = mapper.map(stakedAsset, StakedAssetVO.class);
                        stakedAssetVO.setCreatedAt(stakedAsset.getCreatedAt());
                        stakedAssetVO.setUpdatedAt(stakedAsset.getUpdatedAt());
                        return Mono.just(stakedAssetVO);
                    })
                    .collectList()
                    .map(stakedAssetVOS -> {
                        return APIResponse.builder()
                                .message("Staked assets retrieved successfully")
                                .statusCode(200)
                                .data(stakedAssetVOS)
                                .build();
                    });
        return stakedAssetRepository.findAllByUserIdAndCurrency(userId, currency, PageRequest.of((page-1), size, sort1))
                .flatMap(stakedAsset -> {
                    StakedAssetVO stakedAssetVO = mapper.map(stakedAsset, StakedAssetVO.class);
                    stakedAssetVO.setCreatedAt(stakedAsset.getCreatedAt());
                    stakedAssetVO.setUpdatedAt(stakedAsset.getUpdatedAt());
                    return Mono.just(stakedAssetVO);
                })
                .collectList()
                .map(stakedAssetVOS -> {
                    return APIResponse.builder()
                            .message("Staked assets retrieved successfully")
                            .statusCode(200)
                            .data(stakedAssetVOS)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> getUserStakesByWalletAddress(String userWalletAddress, int page, int size, String sort, String direction) {
        Sort sort1 = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sort);
        return userRepository.findUsersByWalletAddress(userWalletAddress)
                .flatMapMany(user -> stakedAssetRepository.findAllByUserId(user.getId(), PageRequest.of((page-1), size, sort1)))
                .flatMap(stakedAsset -> {
                    StakedAssetVO stakedAssetVO = mapper.map(stakedAsset, StakedAssetVO.class);
                    stakedAssetVO.setCreatedAt(stakedAsset.getCreatedAt());
                    stakedAssetVO.setUpdatedAt(stakedAsset.getUpdatedAt());
                    return Mono.just(stakedAssetVO);
                })
                .collectList()
                .map(stakedAssetVOS -> APIResponse.builder()
                        .message("Staked assets retrieved successfully")
                        .statusCode(200)
                        .data(stakedAssetVOS)
                        .build());
    }

    @Override
    public Mono<APIResponse> getStake(long stakeId) {
        return stakedAssetRepository.findById(stakeId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .statusCode(404)
                                .message("Staked asset not found")
                                .build()
                ))
                .flatMap(stakedAsset -> {
                    StakedAssetVO stakedAssetVO = mapper.map(stakedAsset, StakedAssetVO.class);
                    stakedAssetVO.setCreatedAt(stakedAsset.getCreatedAt());
                    stakedAssetVO.setUpdatedAt(stakedAsset.getUpdatedAt());
                    return Mono.just(stakedAssetVO);
                })
                .map(stakedAssetVO -> APIResponse.builder()
                        .message("Staked asset retrieved successfully")
                        .statusCode(200)
                        .data(stakedAssetVO)
                        .build());
    }

    @Override
    public Mono<APIResponse> updateStake(long stakeId, StakeAssetDto stakeAssetDto) {
        return stakedAssetRepository.findById(stakeId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .statusCode(404)
                                .message("Staked asset not found")
                                .build()
                ))
                .flatMap(stakedAsset -> {
                    stakedAsset.setBalance(stakeAssetDto.getAmount());
                    stakedAsset.setUpdatedAt(LocalDateTime.now());
                    return stakedAssetRepository.save(stakedAsset)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .statusCode(500)
                                            .message("Error updating staked asset")
                                            .build()
                            ));
                })
                .map(stakedAsset -> {
                    StakedAssetVO stakedAssetVO = mapper.map(stakedAsset, StakedAssetVO.class);
                    stakedAssetVO.setCreatedAt(stakedAsset.getCreatedAt());
                    stakedAssetVO.setUpdatedAt(stakedAsset.getUpdatedAt());
                    return APIResponse.builder()
                            .message("Staked asset updated successfully")
                            .statusCode(200)
                            .data(stakedAssetVO)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> requestWithdrawal(StakeWithdrawalDto stakeWithdrawalDto) {
        return null;
    }

    @Override
    public Mono<APIResponse> cancelWithdrawal(long stakedId) {
        return null;
    }
}
