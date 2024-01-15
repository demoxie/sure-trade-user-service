package org.saultech.suretradeuserservice.products.giftcard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.GiftCardDto;
import org.saultech.suretradeuserservice.products.giftcard.service.GiftCardService;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.GIFT_CARDS;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = GIFT_CARDS, produces = "application/json")
@Validated
public class GiftCardController {
    private final GiftCardService giftCardService;

    @PostMapping("/")
    public Mono<APIResponse> addGiftCard(@Valid @RequestBody GiftCardDto dto) {
        return giftCardService.addGiftCard(dto);
    }

    @GetMapping("/")
    public Mono<APIResponse> getGiftCards(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        LoggingService.logRequest("no request", "Product Service", "/gift-cards/", "GET");
        return giftCardService.getGiftCards(page, size, sort, direction);
    }

}
