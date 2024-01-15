package org.saultech.suretradeuserservice.products.giftcard.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.products.giftcard.dto.GetMyGiftCardTransactionsWithOthersDto;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface APIClientService {
    Mono<APIResponse> makeGetRequestWithoutQueryParamsWithMonoReturned(String path, String serviceName, String returnType);

    Mono<APIResponse> makeGetRequestWithQueryParamsWithMonoReturned(String path, String serviceName, Map<String, Object> queryParams, String returnType);

    Mono<APIResponse> makeGetRequestWithQueryParamsAndFluxReturned(String path, String serviceName, Map<String, Object> queryParams, String returnType);


    Mono<APIResponse> makePostRequestWithoutQueryParamsWithMonoReturned(String path, String serviceName, Object body, String returnType);

    Mono<APIResponse> makePostRequestWithoutQueryParamsWithFluxResponse(String s, String product, GetMyGiftCardTransactionsWithOthersDto dto, String returnType);

    Mono<APIResponse> makePutRequestWithoutQueryParamsWithMonoReturned(String s, String product, Object body, String giftCardTransactionVO);
}
