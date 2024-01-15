package org.saultech.suretradeuserservice.products.giftcard.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.GiftCardRateDto;
import reactor.core.publisher.Mono;

public interface GiftCardRateService {
    Mono<APIResponse> getGiftCardRates(int page, int size, String sort, String direction);

    Mono<APIResponse> createGiftCardRate(GiftCardRateDto dto);

    Mono<APIResponse> getMyGiftCardRates(int page, int size, String sort, String direction);
}
