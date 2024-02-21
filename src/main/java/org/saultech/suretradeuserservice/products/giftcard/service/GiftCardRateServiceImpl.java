package org.saultech.suretradeuserservice.products.giftcard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.GiftCardRateDto;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardRateServiceImpl implements GiftCardRateService{
    private final UserRepository userRepository;
    private final APIClientService apiClientService;
    @Override
    public Mono<APIResponse> getGiftCardRates(int page, int size, String sort, String direction) {
        Map<String, Object> queryParams = Map.of("page", page, "size", size, "sort", sort, "direction", direction);
        return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/gift-card-rates/", "product", queryParams,"GiftCardRateVO");
    }

    @Override
    public Mono<APIResponse> createGiftCardRate(GiftCardRateDto dto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                .flatMap(email-> userRepository.findUsersByEmail(email.toString()))
                .flatMap(user -> {
                    dto.setMerchantId(user.getId());
                    return apiClientService.makePostRequestWithoutQueryParamsWithMonoReturned("/gift-card-rates/", "product", dto, "GiftCardRateVO");
                });
    }

    @Override
    public Mono<APIResponse> getMyGiftCardRates(int page, int size, String sort, String direction) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                .flatMap(email-> userRepository.findUsersByEmail(email.toString()))
                .flatMap(user -> {
                    Map<String, Object> queryParams = Map.of("page", page, "size", size, "sort", sort, "direction", direction);
                    return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/gift-card-rates/my-rates/"+user.getId(), "product", queryParams,"GiftCardRateVO");
                });
    }

    @Override
    public Mono<APIResponse> getActiveMerchantRates(
            int page, int size, String sort, String direction
    ) {
        Map<String, Object> queryParams = Map.of(
                "page", page,
                "size", size,
                "sort", sort,
                "direction", direction
        );
        return apiClientService
                .makeGetRequestWithQueryParamsAndFluxReturned("/gift-card-rates/active-merchant-rates",
                        "product",queryParams,"GiftCardRateVO");
    }

}
