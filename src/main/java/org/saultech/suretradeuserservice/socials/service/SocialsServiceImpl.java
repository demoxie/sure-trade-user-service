package org.saultech.suretradeuserservice.socials.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.messaging.email.Email;
import org.saultech.suretradeuserservice.messaging.notification.DeviceNotificationProperties;
import org.saultech.suretradeuserservice.messaging.notification.NotificationData;
import org.saultech.suretradeuserservice.messaging.notification.PushyMessage;
import org.saultech.suretradeuserservice.messaging.sms.Sms;
import org.saultech.suretradeuserservice.messaging.telegram.TelegramMessage;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.dto.RegisterTelegramDto;
import org.saultech.suretradeuserservice.user.repository.UserDeviceDetailsRepository;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
@Slf4j
public class SocialsServiceImpl implements SocialsService{
    private final Producer producer;
    private final UserRepository userRepository;
    private final UserDeviceDetailsRepository userDeviceDetailsRepository;
    private final ModelMapper mapper;

    @Override
    public Mono<APIResponse> registerTelegram(RegisterTelegramDto dto) {
        return userRepository.findUserByUsername(dto.getUsername())
                .switchIfEmpty(Mono.error(APIException.builder()
                        .message("User not found")
                        .statusCode(404)
                        .build()))
                .flatMap(user -> {
                    user.setTelegramChatId(dto.getChatId());
                    return userRepository.save(user)
                            .switchIfEmpty(Mono.error(APIException.builder()
                                    .message("An error occurred while saving your telegram chat id")
                                    .statusCode(500)
                                    .build()))
                            .flatMap(savedUser -> {
                                Email email = Email.builder()
                                        .to(savedUser.getEmail())
                                        .subject("Telegram Account Linked")
                                        .template("telegram-account-linked")
                                        .body(new HashMap<>())
                                        .build();
                                producer.sendEmail(email);
                                Sms sms = Sms.builder()
                                        .to(savedUser.getPhoneNumber())
                                        .body("Your telegram account has been linked successfully")
                                        .build();
                                producer.sendSms(sms);
                                TelegramMessage telegramMessage = TelegramMessage.builder()
                                        .chatId(savedUser.getTelegramChatId())
                                        .message("Your telegram account has been linked successfully")
                                        .build();
                                producer.sendTelegram(telegramMessage);
                                return userDeviceDetailsRepository.findByUserId(savedUser.getId())
                                        .switchIfEmpty(Mono.error(APIException.builder()
                                                .message("User device details not found")
                                                .statusCode(404)
                                                .build()))
                                        .flatMap(userDeviceDetails -> {
                                            log.info("User device details: {}", userDeviceDetails.getDeviceToken());
                                            PushyMessage pushyMessage = PushyMessage.builder()
                                                    .to(userDeviceDetails.getDeviceToken())
                                                    .data(NotificationData.builder()
                                                            .message("Your telegram account has been linked successfully")
                                                            .build())
                                                    .notification(DeviceNotificationProperties.builder()
                                                            .title("Telegram Account Linked")
                                                            .sound("default")
                                                            .badge(1)
                                                            .body("Your telegram account has been linked successfully")
                                                            .build())
                                                    .build();
                                            producer.sendNotification(pushyMessage);
                                            return Mono.just(mapper.map(savedUser, UserProfileVO.class));
                                        });
                            });
                })
                .map(user -> APIResponse.builder()
                        .message("Telegram chat id saved successfully")
                        .statusCode(200)
                        .data(user)
                        .build());
    }
}
