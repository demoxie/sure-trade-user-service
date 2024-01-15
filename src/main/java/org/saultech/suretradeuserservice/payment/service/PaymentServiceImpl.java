package org.saultech.suretradeuserservice.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.payment.dto.PaymentDto;
import org.saultech.suretradeuserservice.payment.enums.PaymentStatus;
import org.saultech.suretradeuserservice.products.giftcard.service.APIClientService;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.enums.Role;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{
    private final APIClientService apiClientService;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final Producer producer;

    @Override
    public Mono<APIResponse> makePayment(PaymentDto paymentDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                .flatMap(principal -> userRepository.findUsersByEmail(principal.toString()))
                .flatMap(user -> {
                    return apiClientService.makePostRequestWithoutQueryParamsWithMonoReturned("/payments/make-payment", "product", paymentDto, "PaymentVO")
                            .flatMap(paymentVOAPIResponse -> {
                                if (paymentVOAPIResponse.getStatusCode() == 200) {
                                    return Mono.just(paymentVOAPIResponse);
                                }
                                return Mono.error(
                                        APIException.builder()
                                                .statusCode(paymentVOAPIResponse.getStatusCode())
                                                .message(paymentVOAPIResponse.getMessage())
                                                .build()
                                );
                            })
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .statusCode(404)
                                            .message("Payment not saved")
                                            .build()
                            ));
                })
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .statusCode(404)
                                .message("User not found")
                                .build()
                ));
    }

    @Override
    public Mono<APIResponse> getPaymentHistory(int page, int size, String sort, String direction) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                .flatMap(principal -> userRepository.findUsersByEmail(principal.toString()))
                .flatMap(user -> {
                    Map<String, Object> params = Map.of(
                            "userId", user.getId(),
                            "merchantId", user.getId(),
                            "page", page,
                            "size", size,
                            "sort", sort,
                            "direction", direction
                    );
                        return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/payments/history", "product", params,"PaymentVO");
                });
    }

    @Override
    public Mono<APIResponse> getUserPaymentHistory(long userId, int page, int size, String sort, String direction) {
        return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/payments/history/users/"+userId, "product", Map.of(
                "page", page,
                "size", size,
                "sort", sort,
                "direction", direction
        ),"PaymentVO");
    }
}
