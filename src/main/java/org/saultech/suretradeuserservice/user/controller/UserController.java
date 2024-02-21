package org.saultech.suretradeuserservice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.service.GiftCardRateService;
import org.saultech.suretradeuserservice.user.dto.ProfileImageDto;
import org.saultech.suretradeuserservice.user.dto.BecomeAMerchantDto;
import org.saultech.suretradeuserservice.user.dto.RegisterTelegramDto;
import org.saultech.suretradeuserservice.user.service.UserService;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.USERS;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = USERS, produces = "application/json")
public class UserController {
    private final UserService userService;
    private final GiftCardRateService giftCardRateService;

    @GetMapping
    public Flux<UserProfileVO> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public Mono<UserProfileVO> getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/logged-in/profile")
    public Mono<UserProfileVO> getMe() {
        return userService.getMe();
    }

    @GetMapping("/merchants/rates/")
    public Mono<APIResponse> getMerchantRates(
            @RequestParam(name = "cardName") String cardName
    ) {
        return userService.getMerchantRates(cardName);
    }

    @PostMapping("/profile/upload-profile-image")
    public Mono<APIResponse> uploadProfileImage(@RequestBody ProfileImageDto dto) {
        return userService.uploadProfileImage(dto);
    }

    @GetMapping("/new-merchant/get-admin-address")
    public Mono<APIResponse> getAdminWalletAddress() {
        return userService.getAdminWalletAddress();
    }

    @PostMapping("/merchant/request/")
    public Mono<APIResponse> becomeMerchant(@Valid @RequestBody BecomeAMerchantDto dto) {
        LoggingService.logRequest(dto, "User Service","/users/merchant/request/", "POST");
        return userService.becomeMerchant(dto);
    }


    @GetMapping("/merchants/active/rate")
    public Mono<APIResponse> getActiveMerchantRates(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        return giftCardRateService.getActiveMerchantRates(page, size, sort, direction);
    }


}
