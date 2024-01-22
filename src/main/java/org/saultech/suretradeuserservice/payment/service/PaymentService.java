package org.saultech.suretradeuserservice.payment.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.payment.dto.ConfirmPaymentDto;
import org.saultech.suretradeuserservice.payment.dto.PaymentDto;
import reactor.core.publisher.Mono;

public interface PaymentService {
    Mono<APIResponse> makePayment(PaymentDto paymentDto);

    Mono<APIResponse> getPaymentHistory(int page, int size, String sort, String direction);

    Mono<APIResponse> getUserPaymentHistory(long userId, int page, int size, String sort, String direction);

    Mono<APIResponse> confirmPayment(long transactionId, ConfirmPaymentDto paymentDto);
}
