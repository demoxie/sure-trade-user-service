package org.saultech.suretradeuserservice.payment.controller;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.payment.dto.PaymentDto;
import org.saultech.suretradeuserservice.payment.service.PaymentService;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.PAYMENTS;

@RestController
@RequestMapping(PAYMENTS)
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/make-payment")
    public Mono<APIResponse> makePayment(@RequestBody PaymentDto paymentDto) {
        LoggingService.logRequest(paymentDto,"Product Service", "/api/v1/products/payments/make-payment", "POST");
        return paymentService.makePayment(paymentDto);
    }

    @GetMapping("/history")
    public Mono<APIResponse> getPaymentHistory(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        return paymentService.getPaymentHistory(page, size, sort, direction);
    }

    @GetMapping("/history/users/{userId}")
    public Mono<APIResponse> getPaymentHistory(
            @PathVariable(value = "userId") long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        return paymentService.getUserPaymentHistory(userId, page, size, sort, direction);
    }
}
