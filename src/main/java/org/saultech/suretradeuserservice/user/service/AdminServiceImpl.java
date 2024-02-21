package org.saultech.suretradeuserservice.user.service;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.messaging.email.Email;
import org.saultech.suretradeuserservice.messaging.notification.NotificationData;
import org.saultech.suretradeuserservice.messaging.notification.PushyMessage;
import org.saultech.suretradeuserservice.messaging.telegram.TelegramMessage;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.entity.UserDeviceDetails;
import org.saultech.suretradeuserservice.user.repository.BecomeMerchantRequestRepository;
import org.saultech.suretradeuserservice.user.repository.UserDeviceDetailsRepository;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.user.vo.BecomeMerchantRequestsVO;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
public class AdminServiceImpl implements AdminService{
    private final UserRepository userRepository;
    private final UserDeviceDetailsRepository userDeviceDetailsRepository;
    private final BecomeMerchantRequestRepository becomeMerchantRequestRepository;
    private final ModelMapper mapper;
    private final Producer producer;

    @Override
    @ReactiveRedisCacheable(cacheName = "getUsers", key = "all")
    public Flux<UserProfileVO> getUsers() {
        return userRepository.findAllByRoles("USER")
                .map(user -> mapper.map(user, UserProfileVO.class));
    }

    @Override
    public Flux<UserProfileVO> getMerchants() {
        return userRepository.findAllByRoles("MERCHANT")
                .map(user -> mapper.map(user, UserProfileVO.class));
    }

    @Override
    public Flux<UserProfileVO> getAdmins() {
        return userRepository.findAllByRoles("ADMIN")
                .map(user -> mapper.map(user, UserProfileVO.class));
    }

    @Override
    public Flux<UserProfileVO> getSuperAdmins() {
        return userRepository.findAllByRoles("SUPER_ADMIN")
                .map(user -> mapper.map(user, UserProfileVO.class));
    }

    @Override
    public Flux<BecomeMerchantRequestsVO> getMerchantRequests() {
        return becomeMerchantRequestRepository.findAll()
                .map(request -> mapper.map(request, BecomeMerchantRequestsVO.class));
    }

    @Override
    public Mono<BecomeMerchantRequestsVO> getMerchantRequestById(long id) {
        return becomeMerchantRequestRepository.findById(id)
                .map(request -> mapper.map(request, BecomeMerchantRequestsVO.class));
    }

    @Override
    public Mono<BecomeMerchantRequestsVO> approveMerchantRequest(long id) {
        return becomeMerchantRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus("APPROVED");
                    return request;
                })
                .flatMap(entity -> becomeMerchantRequestRepository.save(entity)
                        .onErrorResume(e -> Mono.error(
                                APIException.builder()
                                        .statusCode(500)
                                        .message(e.getMessage())
                                        .build()
                        )
                ))
                .flatMap(request -> {
                    Mono<User> userMono = userRepository.findById(request.getUserId())
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .statusCode(404)
                                            .message("User not found")
                                            .build()
                            ))
                            .onErrorResume(e -> Mono.error(
                                    APIException.builder()
                                            .statusCode(500)
                                            .message(e.getMessage())
                                            .build()
                            ))
                            .map(user -> {
                                TelegramMessage telegramMessage = TelegramMessage.builder()
                                        .chatId(user.getTelegramChatId())
                                        .message("Your request to become a merchant has been approved")
                                        .build();
                                producer.sendTelegram(telegramMessage);

                                Map<String, Object> body = Map.of(
                                        "userId", user.getId(),
                                        "role", "MERCHANT",
                                        "name", user.getFirstName() + " " + user.getLastName(),
                                        "username", user.getUsername()
                                );

                                Email email = Email.builder()
                                        .to(user.getEmail())
                                        .subject("Request to become a merchant approved")
                                        .body(body)
                                        .template("become-a-merchant-request-accepted")
                                        .build();
                                producer.sendEmail(email);
                                return user;
                            });
                    Mono<UserDeviceDetails> userDeviceDetailsMono = userDeviceDetailsRepository.findByUserId(request.getUserId())
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .statusCode(404)
                                            .message("User device details not found")
                                            .build()
                            ))
                            .onErrorResume(e -> Mono.error(
                                    APIException.builder()
                                            .statusCode(500)
                                            .message(e.getMessage())
                                            .build()
                            ))
                            .map(userDeviceDetails -> {
                                NotificationData notificationData = NotificationData.builder()
                                        .message("Your request to become a merchant has been approved")
                                        .build();
                                PushyMessage pushyMessage = PushyMessage.builder()
                                        .to(userDeviceDetails.getDeviceToken())
                                        .data(notificationData)
                                        .build();
                                producer.sendNotification(pushyMessage);
                                return userDeviceDetails;
                            });

                    return Mono.zip(userDeviceDetailsMono, userMono)
                            .map(tuple -> request)
                            .map(req -> mapper.map(req, BecomeMerchantRequestsVO.class));

                });
    }
}
