package org.saultech.suretradeuserservice.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.saultech.suretradeuserservice.exception.APIError;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.payment.repository.BankDetailsRepository;
import org.saultech.suretradeuserservice.payment.vo.PaymentVO;
import org.saultech.suretradeuserservice.products.giftcard.dto.GetMyGiftCardTransactionsWithOthersDto;
import org.saultech.suretradeuserservice.products.giftcard.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.products.giftcard.service.APIClientService;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIClientServiceImpl implements APIClientService {
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final ClientSelector clientSelector;
    private final UserRepository userRepository;
    private final BankDetailsRepository bankDetailsRepository;

    @Override
    public Mono<APIResponse> makeGetRequestWithoutQueryParamsWithMonoReturned(String path, String serviceName, String returnType) {
        var returnTypeClass = getReturnTypeClass(returnType);
        return clientSelector.select(serviceName)
                .get()
                .uri(path)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(APIError.class).flatMap(error -> {
                            LoggingService.logError(error,serviceName,path);
                            return Mono.error(
                                    APIException.builder()
                                            .statusCode(clientResponse.statusCode().value())
                                            .message(error.getMessage())
                                            .build()
                            );
                        }))
                .bodyToMono(returnTypeClass)
                .flatMap(apiResponse -> {
                    LoggingService.logResponse(apiResponse,serviceName,path);
                    processGiftCardTransactions(apiResponse);
                    return Mono.just(
                            APIResponse.builder()
                                    .message("Success")
                                    .statusCode(200)
                                    .data(apiResponse)
                                    .build()
                    );
                });
    }

    @Override
    public Mono<APIResponse> makeGetRequestWithQueryParamsWithMonoReturned(String path, String serviceName, Map<String, Object> queryParams, String returnType) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(path);
        queryParams.forEach(uriBuilder::queryParam);
        return clientSelector.select(serviceName)
                .get()
                .uri(uriBuilder.toUriString())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(APIError.class).flatMap(error -> {
                            LoggingService.logError(error,serviceName,path);
                            return Mono.error(
                                    APIException.builder()
                                            .statusCode(clientResponse.statusCode().value())
                                            .message(error.getMessage())
                                            .build()
                            );
                        }))
                .bodyToMono(GiftCardRateVO.class)
                .map(res->{
                    GiftCardRateVO giftCardRateVo = mapper.map(res, GiftCardRateVO.class);
                    LoggingService.logResponse(giftCardRateVo,serviceName,path);
                    return APIResponse.builder()
                            .message("Success")
                            .statusCode(200)
                            .data(giftCardRateVo)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> makeGetRequestWithQueryParamsAndFluxReturned(String path, String serviceName, Map<String, Object> queryParams, String returnType) {
        var returnTypeClass = getReturnTypeClass(returnType);

        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(path);
        queryParams.forEach(uriBuilder::queryParam);
        return clientSelector.select(serviceName)
                .get()
                .uri(uriBuilder.toUriString())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(APIError.class).flatMap(error -> {
                            LoggingService.logError(error,serviceName,path);
                            return Mono.error(
                                    APIException.builder()
                                            .statusCode(clientResponse.statusCode().value())
                                            .message(error.getMessage())
                                            .build()
                            );
                        }))
                .bodyToFlux(returnTypeClass)
                .flatMap(response->{
                    processGiftCardTransactions(response);
                    return Mono.just(response);
                })
                .collectList()
                .flatMap(response -> {
                    LoggingService.logResponse(response,serviceName,path);
                    return Mono.just(
                            APIResponse.builder()
                                    .message("Success")
                                    .statusCode(200)
                                    .data(response)
                                    .build()
                    );
                });
    }

    private static Class<?> getReturnTypeClass(String returnType) {
        return switch (returnType) {
            case "GiftCardTransactionVO" -> GiftCardTransactionVO.class;
            case "GiftCardVO" -> GiftCardVO.class;
            case "GiftCardRateVO" -> GiftCardRateVO.class;
            case "UserProfileVO" -> UserProfileVO.class;
            case "BankDetailsVO" -> BankDetailsVO.class;
            case "StakedAssetVO" -> StakedAssetVO.class;
            case "PaymentVO" -> PaymentVO.class;
            default -> throw APIException.builder()
                    .statusCode(400)
                    .message("Invalid return type")
                    .build();
        };
    }


    @Override
    public Mono<APIResponse> makePostRequestWithoutQueryParamsWithMonoReturned(String path, String serviceName, Object body, String returnType) {
        var returnTypeClass = getReturnTypeClass(returnType);
        return clientSelector.select(serviceName)
                .post()
                .uri(path)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(APIError.class).flatMap(error -> {
                            LoggingService.logError(error,serviceName,path);
                            return Mono.error(
                                    APIException.builder()
                                            .statusCode(clientResponse.statusCode().value())
                                            .message(error.getMessage())
                                            .build()
                            );
                        }))
                .bodyToMono(returnTypeClass)
                .map(res->{
                    LoggingService.logResponse(res,serviceName,path);
                    var response = mapper.map(res, returnTypeClass);
                    return APIResponse.builder()
                            .message("Success")
                            .statusCode(200)
                            .data(response)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> makePostRequestWithoutQueryParamsWithFluxResponse(String s, String product, GetMyGiftCardTransactionsWithOthersDto dto, String returnType) {
        var returnTypeClass = getReturnTypeClass(returnType);
        return clientSelector.select(product)
                .post()
                .uri(s)
                .body(BodyInserters.fromValue(dto))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(APIError.class).flatMap(error -> {
                            LoggingService.logError(error,product,s);
                            return Mono.error(
                                    APIException.builder()
                                            .statusCode(clientResponse.statusCode().value())
                                            .message(error.getMessage())
                                            .build()
                            );
                        }))
                .bodyToFlux(returnTypeClass)
                .publishOn(Schedulers.boundedElastic())
                .map(res->{
                    processGiftCardTransactions(res);
                    return res;
                })
                .collectList()
                .flatMap(apiResponse -> {
                    LoggingService.logResponse(apiResponse,product,s);
                    return Mono.just(
                            APIResponse.builder()
                                    .message("Success")
                                    .statusCode(200)
                                    .data(apiResponse)
                                    .build()
                    );
                });
    }

    @Override
    public Mono<APIResponse> makePutRequestWithoutQueryParamsWithMonoReturned(String s, String product,Object body, String giftCardTransactionVO) {
        var returnTypeClass = getReturnTypeClass(giftCardTransactionVO);
        return clientSelector.select(product)
                .put()
                .uri(s)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(APIError.class).flatMap(error -> {
                            LoggingService.logError(error,product,s);
                            return Mono.error(
                                    APIException.builder()
                                            .statusCode(clientResponse.statusCode().value())
                                            .message(error.getMessage())
                                            .build()
                            );
                        }))
                .bodyToMono(returnTypeClass)
                .map(res->{
                    LoggingService.logResponse(res,product,s);
                    processGiftCardTransactions(res);
                    return APIResponse.builder()
                            .message("Success")
                            .statusCode(200)
                            .data(res)
                            .build();
                });
    }

    private void processGiftCardTransactions(Object res) {
        if(res instanceof GiftCardTransactionVO giftCardTransactionVO){
            if (giftCardTransactionVO.getUserId() != null) {
                userRepository.findById(giftCardTransactionVO.getUserId())
                        .map(user -> {
                            UserProfileVO userProfileVO =  mapper.map(user, UserProfileVO.class);
//                            mapUserProfile(user, userProfileVO);
                            userProfileVO.setCreatedAt(user.getCreatedAt());
                            userProfileVO.setUpdatedAt(user.getUpdatedAt());
                            return userProfileVO;
                        })
                        .subscribe(giftCardTransactionVO::setUser);
            }
            if (giftCardTransactionVO.getMerchantId() != null) {
                userRepository.findById(giftCardTransactionVO.getMerchantId())
                        .map(user -> {
                            UserProfileVO userProfileVO =  mapper.map(user, UserProfileVO.class);
//                            userProfileVO.setCreatedAt(user.getCreatedAt());
//                            userProfileVO.setUpdatedAt(user.getUpdatedAt());
                            return userProfileVO;
                        })
                        .subscribe(giftCardTransactionVO::setMerchant);
            }
            if (giftCardTransactionVO.getBankDetailsId() != null) {
                bankDetailsRepository.findById(giftCardTransactionVO.getBankDetailsId())
                        .map(bankDetails -> {
                            BankDetailsVO bankDetailsVO = mapper.map(bankDetails, BankDetailsVO.class);
//                            bankDetailsVO.setCreatedAt(bankDetails.getCreatedAt());
//                            bankDetailsVO.setUpdatedAt(bankDetails.getUpdatedAt());
                            return bankDetailsVO;
                        })
                        .subscribe(giftCardTransactionVO::setBankDetails);
            }
            if (giftCardTransactionVO.getGiftCardRateId() != null) {
                bankDetailsRepository.findById(giftCardTransactionVO.getGiftCardRateId())
                        .map(giftCardRate -> {
                            GiftCardRateVO giftCardRateVO = mapper.map(giftCardRate, GiftCardRateVO.class);
//                            giftCardRateVO.setCreatedAt(giftCardRate.getCreatedAt());
//                            giftCardRateVO.setUpdatedAt(giftCardRate.getUpdatedAt());
                            return giftCardRateVO;
                        })
                        .subscribe(giftCardTransactionVO::setGiftCardRate);
            }
//            giftCardTransactionVO.setCreatedAt(giftCardTransactionVO.getCreatedAt());
//            giftCardTransactionVO.setUpdatedAt(giftCardTransactionVO.getUpdatedAt());
        }
    }

    private void mapUserProfile(User user, UserProfileVO userProfileVO) {
        userProfileVO.setCreatedAt(user.getCreatedAt());
        userProfileVO.setUpdatedAt(user.getUpdatedAt());
        userProfileVO.setFirstName(user.getFirstName());
        userProfileVO.setLastName(user.getLastName());
        userProfileVO.setPhoneNumber(user.getPhoneNumber());
        userProfileVO.setCountry(user.getCountry());
        userProfileVO.setCity(user.getCity());
        userProfileVO.setAddress(user.getAddress());
    }

}
