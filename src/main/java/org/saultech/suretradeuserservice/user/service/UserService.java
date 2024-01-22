package org.saultech.suretradeuserservice.user.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.user.dto.BecomeAMerchantDto;
import org.saultech.suretradeuserservice.user.dto.ProfileImageDto;
import org.saultech.suretradeuserservice.user.dto.RegisterTelegramDto;
import org.saultech.suretradeuserservice.user.dto.UserDto;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveUserDetailsService {
    Flux<UserProfileVO> getUsers();

    Mono<UserProfileVO> getUserById(long id);

    Mono<User> getUserByUsername(String username);

    Mono<User> getUserByEmail(String email);

    Mono<User> createUser(UserDto registerDto);

    Mono<UserProfileVO> getMe();

    Mono<User> getCurrentUser();

    Mono<APIResponse> getMerchantRates(String cardName);

    Mono<APIResponse> uploadProfileImage(ProfileImageDto dto);

    Mono<APIResponse> getAdminWalletAddress();

    Mono<APIResponse> becomeMerchant(BecomeAMerchantDto dto);

}
