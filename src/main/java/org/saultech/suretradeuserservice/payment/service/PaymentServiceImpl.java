package org.saultech.suretradeuserservice.payment.service;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.business.repository.StakedAssetRepository;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.messaging.email.Email;
import org.saultech.suretradeuserservice.messaging.notification.NotificationData;
import org.saultech.suretradeuserservice.messaging.notification.PushyMessage;
import org.saultech.suretradeuserservice.messaging.sms.Sms;
import org.saultech.suretradeuserservice.messaging.telegram.TelegramMessage;
import org.saultech.suretradeuserservice.payment.dto.ConfirmPaymentDto;
import org.saultech.suretradeuserservice.payment.dto.PaymentDto;
import org.saultech.suretradeuserservice.payment.vo.PaymentVO;
import org.saultech.suretradeuserservice.products.giftcard.service.APIClientService;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.entity.UserDeviceDetails;
import org.saultech.suretradeuserservice.user.repository.UserDeviceDetailsRepository;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final APIClientService apiClientService;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final UserDeviceDetailsRepository userDeviceDetailsRepository;
    private final StakedAssetRepository stakedAssetRepository;
    private final Producer producer;

    @Override
    public Mono<APIResponse> makePayment(PaymentDto paymentDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                .flatMap(principal -> userRepository.findUsersByEmail(principal.toString()))
                .flatMap(user -> stakedAssetBalanceDeductible(paymentDto.getAmount(), user.getId())
                        .flatMap(isDeductible -> {
                            if (Boolean.TRUE.equals(isDeductible)) {
                                return apiClientService.makePostRequestWithoutQueryParamsWithMonoReturned("/payments/make-payment", "product", paymentDto, "PaymentVO")
                                        .switchIfEmpty(Mono.error(
                                                APIException.builder()
                                                        .statusCode(500)
                                                        .message("An error occurred while making payment")
                                                        .build()
                                        ))
                                        .onErrorResume(throwable -> Mono.error(
                                                APIException.builder()
                                                        .statusCode(500)
                                                        .message(throwable.getMessage())
                                                        .build()
                                        ))
                                        .flatMap(
                                                apiResponse -> {
                                                    PaymentVO paymentVO = (PaymentVO) apiResponse.getData();

                                                    return stakedAssetRepository.findByUserId(user.getId())
                                                            .flatMap(stakedAsset -> {
                                                                stakedAsset.setPreviousBalance(stakedAsset.getBalance());
                                                                stakedAsset.setBalance(stakedAsset.getBalance().subtract(paymentDto.getAmount()));
                                                                return stakedAssetRepository.save(stakedAsset)
                                                                        .switchIfEmpty(Mono.error(
                                                                                APIException.builder()
                                                                                        .statusCode(500)
                                                                                        .message("An error occurred while saving staked asset")
                                                                                        .build()
                                                                        ))
                                                                        .onErrorResume(throwable -> Mono.error(
                                                                                APIException.builder()
                                                                                        .statusCode(500)
                                                                                        .message(throwable.getMessage())
                                                                                        .build()
                                                                        ));
                                                            })
                                                            .flatMap(stakedAsset -> {
                                                                var userDeviceDetail = userRepository.findById(paymentVO.getUserId())
                                                                        .flatMap(user1 -> sendMessageToUser(paymentDto, user, user1, paymentVO)).map(response -> response);

                                                                return sendFeedBackMessageAndReturnApiResponse(paymentDto, user, apiResponse, paymentVO, userDeviceDetail);
                                                            });

                                                });
                            }
                            return Mono.error(
                                    APIException.builder()
                                            .statusCode(400)
                                            .message("Insufficient balance")
                                            .build()
                            );
                        }))
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .statusCode(404)
                                .message("User not found")
                                .build()
                ));
    }

    @NotNull
    private Mono<APIResponse> sendFeedBackMessageAndReturnApiResponse(PaymentDto paymentDto, User user, APIResponse apiResponse, PaymentVO paymentVO, Mono<UserDeviceDetails> userDeviceDetail) {
        Map<String, Object> body = Map.of(
                "name", user.getUsername(),
                "amount", paymentVO.getCurrency() + paymentDto.getAmount(),
                "paymentMethod", paymentDto.getPaymentMethod()
        );

        Email feedBackEmail = Email.builder()
                .to(user.getEmail())
                .subject("Payment Successful")
                .template("payment-successful")
                .body(body)
                .build();
        producer.sendEmail(feedBackEmail);
        Sms sms = Sms.builder()
                .to(user.getPhoneNumber())
                .body("""
                        Hi %s,
                        Your payment of %s was successful
                        """.formatted(user.getUsername(), paymentVO.getCurrency() + paymentDto.getAmount())).build();

        producer.sendSms(sms);

        TelegramMessage telegramMessage = TelegramMessage.builder()
                .chatId(user.getTelegramChatId())
                .message("""
                        Hi %s,
                        Your payment of %s was successful
                        """.formatted(user.getUsername(), paymentVO.getCurrency() + paymentDto.getAmount()))
                .build();
        producer.sendTelegram(telegramMessage);
        var feedBackNotification = userDeviceDetailsRepository.findByUserId(user.getId())
                .flatMap(userDeviceDetails -> {
                    NotificationData notificationData = NotificationData.builder()
                            .message("""
                                    Hi %s,
                                    Your payment of %s was successful
                                    """.formatted(user.getUsername(), paymentVO.getCurrency() + paymentDto.getAmount()))
                            .build();
                    PushyMessage pushyMessage = PushyMessage.builder()
                            .to(userDeviceDetails.getDeviceToken())
                            .data(notificationData)
                            .build();
                    producer.sendNotification(pushyMessage);
                    return Mono.just(userDeviceDetails);
                });
        return Mono.zip(userDeviceDetail, feedBackNotification)
                .flatMap(tuple -> Mono.just(apiResponse));
    }

    @NotNull
    private Mono<UserDeviceDetails> sendMessageToUser(PaymentDto paymentDto, User user, User user1, PaymentVO paymentVO) {
        Map<String, Object> body = Map.of(
                "name", user1.getUsername(),
                "sender", user.getUsername(),
                "amount", paymentVO.getCurrency() + paymentDto.getAmount(),
                "paymentMethod", paymentDto.getPaymentMethod()
        );

        Email emailToPayer = Email.builder()
                .to(user1.getEmail())
                .subject("Paid")
                .template("paid")
                .body(body)
                .build();
        producer.sendEmail(emailToPayer);
        Sms sms = Sms.builder()
                .to(user1.getPhoneNumber())
                .body("""
                        Hi %s,
                        You just received a payment of %s from %s
                        """.formatted(user1.getUsername(), paymentVO.getCurrency() + paymentDto.getAmount(), user.getUsername()))
                .build();
        producer.sendSms(sms);

        TelegramMessage telegramMessage = TelegramMessage.builder()
                .chatId(user1.getTelegramChatId())
                .message("""
                        Hi %s,
                        You just received a payment of %s from %s
                        """.formatted(user1.getUsername(), paymentVO.getCurrency() + paymentDto.getAmount(), user1.getUsername()))
                .build();
        producer.sendTelegram(telegramMessage);
        return userDeviceDetailsRepository.findByUserId(user1.getId())
                .flatMap(userDeviceDetails -> {
                    NotificationData notificationData = NotificationData.builder()
                            .message("""
                                    Hi %s,
                                    You just received a payment of %s from %s
                                    """.formatted(user1.getUsername(), paymentVO.getCurrency() + paymentDto.getAmount(), user.getUsername()))
                            .build();
                    PushyMessage pushyMessage = PushyMessage.builder()
                            .to(userDeviceDetails.getDeviceToken())
                            .data(notificationData)
                            .build();
                    producer.sendNotification(pushyMessage);
                    return Mono.just(userDeviceDetails);
                });
    }

    @Override
    @ReactiveRedisCacheable(cacheName = "payment", key = "#userId + #page + #size + #sort + #direction")
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
                    return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/payments/history", "product", params, "PaymentVO");
                });
    }

    @Override
    @ReactiveRedisCacheable(cacheName = "payment", key = "#userId + #page + #size + #sort + #direction")
    public Mono<APIResponse> getUserPaymentHistory(long userId, int page, int size, String sort, String direction) {
        return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/payments/history/users/" + userId, "product", Map.of(
                "page", page,
                "size", size,
                "sort", sort,
                "direction", direction
        ), "PaymentVO");
    }

    @Override
    public Mono<APIResponse> confirmPayment(long transactionId, ConfirmPaymentDto paymentDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                .flatMap(principal -> userRepository.findUsersByEmail(principal.toString()))
                .flatMap(user -> apiClientService.makePostRequestWithoutQueryParamsWithMonoReturned("/payments/transactions/" + transactionId + "/confirm-payment", "product", paymentDto, "PaymentVO")
                        .switchIfEmpty(Mono.error(
                                APIException.builder()
                                        .statusCode(500)
                                        .message("An error occurred while confirming payment")
                                        .build()
                        ))
                        .onErrorResume(throwable -> {
                            var exception = (APIException) throwable;
                            return Mono.error(exception);
                        })
                        .flatMap(
                                apiResponse -> {
                                    PaymentVO paymentVO = (PaymentVO) apiResponse.getData();
                                    return userRepository.findById(paymentVO.getMerchantId())
                                            .flatMap(merchant -> {
                                                Map<String, Object> body = Map.of(
                                                        "name", merchant.getUsername(),
                                                        "receiver", user.getUsername(),
                                                        "amount", paymentVO.getCurrency() + paymentVO.getAmount()
                                                );

                                                Email emailToPayer = Email.builder()
                                                        .to(merchant.getEmail())
                                                        .subject("Payment Confirmed")
                                                        .template("payment-confirmed")
                                                        .body(body)
                                                        .build();
                                                producer.sendEmail(emailToPayer);
                                                Sms sms = Sms.builder()
                                                        .to(merchant.getPhoneNumber())
                                                        .body("""
                                                                Hi %s,
                                                                Your payment of %s was confirmed
                                                                """.formatted(merchant.getUsername(), paymentVO.getCurrency() + paymentVO.getAmount()))
                                                        .build();
                                                producer.sendSms(sms);

                                                TelegramMessage telegramMessage = TelegramMessage.builder()
                                                        .chatId(merchant.getTelegramChatId())
                                                        .message("""
                                                                Hi %s,
                                                                Your payment of %s was confirmed
                                                                """.formatted(merchant.getUsername(), paymentVO.getCurrency() + paymentVO.getAmount()))
                                                        .build();
                                                producer.sendTelegram(telegramMessage);
                                                return userDeviceDetailsRepository.findByUserId(merchant.getId())
                                                        .flatMap(userDeviceDetails -> {
                                                            NotificationData notificationData = NotificationData.builder()
                                                                    .message("""
                                                                            Hi %s,
                                                                            Your payment of %s was confirmed
                                                                            """.formatted(merchant.getUsername(), paymentVO.getCurrency() + paymentVO.getAmount()))
                                                                    .build();
                                                            PushyMessage pushyMessage = PushyMessage.builder()
                                                                    .to(userDeviceDetails.getDeviceToken())
                                                                    .data(notificationData)
                                                                    .build();
                                                            producer.sendNotification(pushyMessage);
                                                            return Mono.just(userDeviceDetails);
                                                        });
                                            })
                                            .flatMap(userDeviceDetails -> Mono.just(apiResponse));
                                }));
    }

    private Mono<Boolean> stakedAssetBalanceDeductible(BigDecimal amount, long userId) {
        return stakedAssetRepository.findByUserId(userId)
                .filter(stakedAsset -> stakedAsset.getBalance().compareTo(amount) >= 0)
                .hasElement()
                .map(isDeductible -> isDeductible);
    }
}
