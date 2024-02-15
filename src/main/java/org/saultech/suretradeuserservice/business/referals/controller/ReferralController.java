package org.saultech.suretradeuserservice.business.referals.controller;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.business.referals.dto.ReferralDto;
import org.saultech.suretradeuserservice.business.referals.service.ReferralService;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/referrals")
@RequiredArgsConstructor
public class ReferralController {
    private final ReferralService referralService;

    @GetMapping("/my/referrals")
    public Mono<APIResponse> getMyReferrals() {
        return referralService.getMyReferrals();
    }

    @GetMapping("/my/referralCodes/{referralType}")
    public Mono<APIResponse> getMyReferralCodes(String referralType) {
        return referralService.getMyReferralCodes(referralType);
    }

    @GetMapping("/referrer/{referralCode}")
    public Mono<APIResponse> getReferralsByReferralCode(String referralCode) {
        return referralService.getReferralsByReferralCode(referralCode);
    }

    @PutMapping("/activate-referral/{referralCode}")
    public Mono<APIResponse> activateReferral(@PathVariable String referralCode, ReferralDto referralDto) {
        return referralService.activateReferral(referralCode, referralDto.getReferralType());
    }
}
