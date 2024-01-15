package org.saultech.suretradeuserservice.rabbitmq.service;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.config.app.AppConfig;
import org.saultech.suretradeuserservice.products.giftcard.dto.CreateGiftCardTransactionDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Producer {
    private final RabbitTemplate rabbitTemplate;
    private final AppConfig appConfig;

    public void sendNotification(Object message) {
        rabbitTemplate.convertAndSend(appConfig.getNotification().getQueue(), message);
    }

    public void sendEmail(Object message) {
        rabbitTemplate.convertAndSend(appConfig.getEmail().getQueue(), message);
    }

    public void sendSms(Object message) {
        rabbitTemplate.convertAndSend(appConfig.getSms().getQueue(), message);
    }

    public void sendTelegram(String message) {
        rabbitTemplate.send(appConfig.getTelegram().getExchange(), appConfig.getTelegram().getRoutingKey(), new Message(message.getBytes()));
    }

    public void sendTelegramActivation(String message) {
        rabbitTemplate.send(appConfig.getTelegramActivation().getExchange(), appConfig.getTelegramActivation().getRoutingKey(), new Message(message.getBytes()));
    }

    public void sendWhatsapp(String message) {
        rabbitTemplate.convertAndSend(appConfig.getWhatsapp().getQueue(), message);
    }

    public void sendToGiftCardTransactionQueue(CreateGiftCardTransactionDto createGiftCardTransactionDto) {
        rabbitTemplate.convertAndSend(appConfig.getGiftCardTransaction().getQueue(), createGiftCardTransactionDto);
    }
}
