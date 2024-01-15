package org.saultech.suretradeuserservice.products.giftcard.controller;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.GiftCardRateDto;
import org.saultech.suretradeuserservice.products.giftcard.service.GiftCardRateService;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.GIFT_CARDS;
import static org.saultech.suretradeuserservice.constants.BaseRoutes.GIFT_CARD_RATES;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = GIFT_CARD_RATES, produces = "application/json")
@Validated
public class GiftCardRateController {
    private final GiftCardRateService giftCardRateService;

    @GetMapping("/")
    public Mono<APIResponse> getGiftCardRates(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        LoggingService.logRequest("no request", "Product Service", "/api/v2/gift-card-rates/", "GET");
        return giftCardRateService.getGiftCardRates(page, size, sort, direction);
    }

    @PostMapping("/")
    public Mono<APIResponse> createGiftCardRate(@RequestBody GiftCardRateDto dto) {
        LoggingService.logRequest(dto, "Product Service", "/gift-card-rates/", "POST");
        return giftCardRateService.createGiftCardRate(dto);
    }

    @GetMapping("/merchants/mine")
    public Mono<APIResponse> getMyGiftCardRates(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        LoggingService.logRequest("no request", "Product Service", "/api/v2/gift-card-rates/merchants/mine", "GET");
        return giftCardRateService.getMyGiftCardRates(page, size, sort, direction);
    }
}
