package org.saultech.suretradeuserservice.products.giftcard.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.AcceptRejectTransactionDto;
import org.saultech.suretradeuserservice.products.giftcard.dto.CancelRequestDto;
import org.saultech.suretradeuserservice.products.giftcard.dto.CreateGiftCardTransactionDto;
import org.saultech.suretradeuserservice.products.giftcard.dto.GiftCardDto;
import reactor.core.publisher.Mono;

public interface GiftCardService {
    Mono<APIResponse> getGiftCardTransactions(int page, int size, String sort, String direction);

    Mono<APIResponse> createGiftCardTransaction(CreateGiftCardTransactionDto createGiftCardTransactionDto);

    Mono<APIResponse> getGiftCardTransaction(Long id);

    Mono<APIResponse> getGiftCardTransactionByReferenceNo(String referenceNo);

    Mono<APIResponse> getGiftCardTransactionsWithOthers(int page, int size, String sort, String direction);

    Mono<APIResponse> getMyGiftCardTransactions(int page, int size, String sort, String direction);

    Mono<APIResponse> addGiftCard(GiftCardDto dto);

    Mono<APIResponse> getGiftCards(int page, int size, String sort, String direction);

    Mono<APIResponse> getGiftCardTransactionsByStatus(String status, int page, int size, String sort, String direction);

    Mono<APIResponse> cancelMyGiftCardTransaction(Long transactionId, CancelRequestDto dto);

    Mono<APIResponse> acceptOrCancelTransaction(long transactionId, AcceptRejectTransactionDto dto);
}
