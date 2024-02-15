package org.saultech.suretradeuserservice.business.referals.service;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.business.referals.entity.Referral;
import org.saultech.suretradeuserservice.business.referals.enums.ReferralStatus;
import org.saultech.suretradeuserservice.business.referals.enums.ReferralType;
import org.saultech.suretradeuserservice.business.referals.repository.ReferralRepository;
import org.saultech.suretradeuserservice.business.referals.vo.ReferralVO;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.config.app.AppConfig;
import org.saultech.suretradeuserservice.config.app.Business;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.utils.ErrorUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferralServiceImpl implements ReferralService {
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final AppConfig appConfig;

    @Override
    public Mono<APIResponse> createReferral(Long userId, Long referredUserId) {
        return null;
    }

    @Override
    public Mono<APIResponse> approveReferral(Long referralId) {
        return null;
    }

    @Override
    public Mono<APIResponse> rejectReferral(Long referralId) {
        return null;
    }

    @Override
    public Mono<APIResponse> getReferrals(Long userId) {
        return null;
    }

    @Override
    public Mono<APIResponse> getReferral(Long referralId) {
        return null;
    }

    @Override
    public Mono<APIResponse> getReferralStatus(Long referralId) {
        return null;
    }

    @Override
    public Mono<APIResponse> getReferralCount(Long userId) {
        return null;
    }

    @Override
    public Mono<APIResponse> getReferralCountByStatus(Long userId, String status) {
        return null;
    }

    @Override
    public Mono<APIResponse> getReferralCountByStatus(String status) {
        return null;
    }

    @Override
    @ReactiveRedisCacheable(cacheName = "referrals", key = "#userId")
    public Mono<APIResponse> getMyReferrals() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> referralRepository.findAllByReferralCodeAndReferee(user.getReferralCodes(), user.getId())
                        .switchIfEmpty(
                                Mono.error(
                                        APIException.builder()
                                                .message("No referrals found")
                                                .statusCode(404)
                                                .build()
                                )
                        )
                        .onErrorResume(throwable -> {
                            log.error("Error getting referrals", throwable);
                            return Mono.error(
                                    APIException.builder()
                                            .message(ErrorUtils.getErrorMessage(throwable))
                                            .statusCode(ErrorUtils.getStatusCode(throwable))
                                            .build()
                            );
                        })
                        .collectList())
                .flatMap(referrals -> {
                    log.info("Referrals retrieved successfully");
                    return Mono.just(
                            APIResponse.builder()
                                    .message("Referrals retrieved successfully")
                                    .statusCode(200)
                                    .data(referrals)
                                    .build()
                    );
                });
    }

    @Override
    @ReactiveRedisCacheable(cacheName = "referralCodes", key = "#referralType")
    public Mono<APIResponse> getMyReferralCodes(String referralType) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> {
                    ReferralVO referralVO = ReferralVO.builder()
                            .referralType(ReferralType.valueOf(referralType))
                            .referralCode(user.getReferralCodes())
                            .build();
                    return Mono.just(referralVO);
                })
                .flatMap(referralVO -> {
                    log.info("Referral codes retrieved successfully");
                    return Mono.just(
                            APIResponse.builder()
                                    .message("Referral codes retrieved successfully")
                                    .statusCode(200)
                                    .data(referralVO)
                                    .build()
                    );
                });
    }

    @Override
    @ReactiveRedisCacheable(cacheName = "referrals", key = "#referralCode")
    public Mono<APIResponse> getReferralsByReferralCode(String referralCode) {
        return referralRepository.findAllByReferralCode(referralCode)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("No referrals found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Error getting referrals", throwable);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(throwable))
                                    .statusCode(ErrorUtils.getStatusCode(throwable))
                                    .build()
                    );
                })
                .flatMap(referrals -> {
                    return Mono.just(
                            mapper.map(referrals, ReferralVO.class)
                    );
                })
                .collectList()
                .map(referrals -> {
                    log.info("Referrals retrieved successfully");
                    return APIResponse.builder()
                            .message("Referrals retrieved successfully")
                            .statusCode(200)
                            .data(referrals)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> activateReferral(String referralCode, ReferralType referralType) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(referrer -> {
                    return userRepository.findUserByUsername(referralCode)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("Referral code not found")
                                            .statusCode(404)
                                            .build()
                            ))
                            .onErrorResume(throwable -> {
                                log.error("Error getting user", throwable);
                                return Mono.error(
                                        APIException.builder()
                                                .message(ErrorUtils.getErrorMessage(throwable))
                                                .statusCode(ErrorUtils.getStatusCode(throwable))
                                                .build()
                                );
                            })
                            .flatMap(user -> referralRepository.findAllByReferrerAndReferralType(user.getId(), referralType)
                                    .onErrorResume(throwable -> {
                                        log.error("Error getting referrals", throwable);
                                        return Mono.error(
                                                APIException.builder()
                                                        .message(ErrorUtils.getErrorMessage(throwable))
                                                        .statusCode(ErrorUtils.getStatusCode(throwable))
                                                        .build()
                                        );
                                    })
                                    .flatMap(referrals -> {
                                        if (referrals != null) {
                                            return Mono.error(
                                                    APIException.builder()
                                                            .message("Referral already activated")
                                                            .statusCode(404)
                                                            .build()
                                            );
                                        }
                                        return Mono.empty();
                                    })
                                    .switchIfEmpty(
                                            Mono.defer(() -> {
                                                Business business = appConfig.getBusiness();
                                                Referral referral = Referral.builder()
                                                        .referrer(referrer.getId())
                                                        .referee(user.getId())
                                                        .referralCode(user.getReferralCodes())
                                                        .referralLink(business.getGooglePlayAppLink())
                                                        .referralType(referralType)
                                                        .status(ReferralStatus.UNPAID)
                                                        .expiryDate(LocalDate.now().plusDays(business.getSignUpReferralExpiryDate()))
                                                        .referralValue(business.getSignUpReferralValue())
                                                        .build();
                                                return referralRepository.save(referral)
                                                        .onErrorResume(throwable -> {
                                                            log.error("Error saving referral", throwable);
                                                            return Mono.error(
                                                                    APIException.builder()
                                                                            .message(ErrorUtils.getErrorMessage(throwable))
                                                                            .statusCode(ErrorUtils.getStatusCode(throwable))
                                                                            .build()
                                                            );
                                                        });
                                            })
                                    )
                                    .map(referrals -> {
                                        log.info("Referrals retrieved successfully");
                                        return APIResponse.builder()
                                                .message("Referrals retrieved successfully")
                                                .statusCode(200)
                                                .data(referrals)
                                                .build();
                                    }));
                });
    }
}
