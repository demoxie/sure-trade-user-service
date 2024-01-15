package org.saultech.suretradeuserservice.products.giftcard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.AcceptRejectTransactionDto;
import org.saultech.suretradeuserservice.products.giftcard.dto.CancelRequestDto;
import org.saultech.suretradeuserservice.products.giftcard.dto.CreateGiftCardTransactionDto;
import org.saultech.suretradeuserservice.products.giftcard.service.GiftCardService;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.GIFT_CARD_TRANSACTIONS;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = GIFT_CARD_TRANSACTIONS, produces = "application/json")
@Validated
public class GiftCardTransactionController {
    private final GiftCardService giftCardService;

    @GetMapping("/")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Mono<APIResponse> getGiftCardTransactions(        @RequestParam(name = "page", defaultValue = "1") int page,
                                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                                             @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
                                                             @RequestParam(name = "direction", defaultValue = "DESC") String direction) {
        return giftCardService.getGiftCardTransactions(page, size, sort, direction);
    }

    @PostMapping("/")
    public Mono<APIResponse> createGiftCardTransaction(@Valid @RequestBody CreateGiftCardTransactionDto dto) {
        return giftCardService.createGiftCardTransaction(dto);
    }

    @GetMapping("/{id}")
    public Mono<APIResponse> getGiftCardTransaction(@PathVariable Long id) {
        LoggingService.logRequest(id, "Product Service", "/api/v2/products/gift-card/transaction/{id}", "GET");
        return giftCardService.getGiftCardTransaction(id);
    }

    @GetMapping("/references/{referenceNo}")
    public Mono<APIResponse> getGiftCardTransactionByReferenceNo(@PathVariable String referenceNo) {
        return giftCardService.getGiftCardTransactionByReferenceNo(referenceNo);
    }

    @GetMapping("/mine/with/others")
    public Mono<APIResponse> getGiftCardTransactionsWithOthers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        return giftCardService.getGiftCardTransactionsWithOthers(page, size, sort, direction);
    }

    @PutMapping("/mine/logged-in-user")
    public Mono<APIResponse> getMyGiftCardTransactions(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        return giftCardService.getMyGiftCardTransactions(page, size, sort, direction);
    }

    @PutMapping("/filter/{status}")
    public Mono<APIResponse> getGiftCardTransactionsByStatus(
            @PathVariable String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        return giftCardService.getGiftCardTransactionsByStatus(status, page, size, sort, direction);
    }

    @PutMapping("/my-transactions/{transactionId}/cancel")
    public Mono<APIResponse> cancelMyGiftCardTransaction(@PathVariable Long transactionId, @RequestBody CancelRequestDto dto) {
        return giftCardService.cancelMyGiftCardTransaction(transactionId, dto);
    }

    @PutMapping("/{transactionId}/accept-or-reject")
    public Mono<APIResponse> acceptOrCancelTransaction(@PathVariable long transactionId, @RequestBody AcceptRejectTransactionDto dto){
        return giftCardService.acceptOrCancelTransaction(transactionId, dto);
    }

}
