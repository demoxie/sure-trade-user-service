package org.saultech.suretradeuserservice.business.referals.service;

import org.saultech.suretradeuserservice.business.referals.enums.ReferralType;
import org.saultech.suretradeuserservice.common.APIResponse;
import reactor.core.publisher.Mono;

public interface ReferralService {
    Mono<APIResponse> createReferral(Long userId, Long referredUserId);

    Mono<APIResponse> approveReferral(Long referralId);

    Mono<APIResponse> rejectReferral(Long referralId);

    Mono<APIResponse> getReferrals(Long userId);

    Mono<APIResponse> getReferral(Long referralId);

    Mono<APIResponse> getReferralStatus(Long referralId);

    Mono<APIResponse> getReferralCount(Long userId);

    Mono<APIResponse> getReferralCountByStatus(Long userId, String status);

    Mono<APIResponse> getReferralCountByStatus(String status);

    Mono<APIResponse> getMyReferrals();

    Mono<APIResponse> getMyReferralCodes(String referralType);

    Mono<APIResponse> getReferralsByReferralCode(String referralCode);

    Mono<APIResponse> activateReferral(String referralCode, ReferralType referralType);
}
