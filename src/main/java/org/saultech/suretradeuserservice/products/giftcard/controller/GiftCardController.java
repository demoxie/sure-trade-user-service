package org.saultech.suretradeuserservice.products.giftcard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.GiftCardDto;
import org.saultech.suretradeuserservice.products.giftcard.service.GiftCardService;
import org.saultech.suretradeuserservice.products.giftcard.vo.SupportedGiftCardsVO;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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

    @GetMapping("/{id}")
    public Mono<APIResponse> getGiftCardById(@PathVariable long id) {
        LoggingService.logRequest(id, "Product Service", "/gift-cards/" + id, "GET");
        return giftCardService.getGiftCardById(id);
    }

    @PutMapping("/{id}")
    public Mono<APIResponse> updateGiftCard(@PathVariable Long id, @Valid @RequestBody GiftCardDto dto) {
        LoggingService.logRequest(dto, "Product Service", "/gift-cards/" + id, "PUT");
        return giftCardService.updateGiftCard(id, dto);
    }

    @DeleteMapping("/{id}")
    public Mono<APIResponse> deleteGiftCard(@PathVariable Long id) {
        return giftCardService.deleteGiftCard(id);
    }

    @GetMapping("/mine")
    public Mono<APIResponse> getMyGiftCards(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        return giftCardService.getMyGiftCards(page, size, sort, direction);
    }

    @GetMapping("/params/search/")
    public Mono<APIResponse> searchSupportedGiftCards(@RequestParam(name = "name") String name,
                                                               @RequestParam(name = "currency") String currency,
                                                               @RequestParam(name = "page", defaultValue = "1") int page,
                                                               @RequestParam(name = "size", defaultValue = "10") int size,
                                                               @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
                                                               @RequestParam(name = "direction", defaultValue = "DESC") String direction) {
        LoggingService.logRequest("no request", "Product Service", "/gift-cards/params/search", "GET");
        return giftCardService.searchSupportedGiftCards(name, currency, page, size, sort, direction);
    }

    @GetMapping("/supported/gift-cards")
    public Mono<APIResponse> getSupportedGiftCards(@RequestParam(name = "page", defaultValue = "1") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                                            @RequestParam(name = "sort", defaultValue = "name") String sort,
                                                            @RequestParam(name = "direction", defaultValue = "DESC") String direction) {
        LoggingService.logRequest("no request", "Product Service", "/gift-cards/supported/gift-cards", "GET");
        return giftCardService.getSupportedGiftCards(page, size, sort, direction);
    }

}
