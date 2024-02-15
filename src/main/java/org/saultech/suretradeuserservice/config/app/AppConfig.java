package org.saultech.suretradeuserservice.config.app;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@Data
public class AppConfig {
    private Email email;
    private Telegram telegram;
    private TelegramActivation telegramActivation;
    private Whatsapp whatsapp;
    private Notification notification;
    private Sms sms;
    private GiftCardTransaction giftCardTransaction;

    private Business business;
}
