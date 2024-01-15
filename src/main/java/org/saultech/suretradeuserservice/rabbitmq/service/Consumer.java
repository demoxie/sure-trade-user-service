//package org.saultech.suretradeuserservice.rabbitmq.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.rabbitmq.client.Channel;
//import com.saultech.messagingservice.email.entity.Email;
//import com.saultech.messagingservice.email.service.EmailSenderService;
//import com.saultech.messagingservice.firebase.entity.FirebaseNotification;
//import com.saultech.messagingservice.firebase.service.FirebaseService;
//import com.saultech.messagingservice.onesignal.entity.NotificationMessage;
//import com.saultech.messagingservice.onesignal.entity.OneSignalMessage;
//import com.saultech.messagingservice.onesignal.service.OneSignalService;
//import com.saultech.messagingservice.pushy.entity.PushyMessage;
//import com.saultech.messagingservice.pushy.service.PushyService;
//import com.saultech.messagingservice.telegram.entity.TelegramMessage;
//import com.saultech.messagingservice.telegram.service.TelegramService;
//import com.saultech.messagingservice.twilio.entity.TwilioMessage;
//import jakarta.mail.MessagingException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeoutException;
//
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class Consumer {
//    private final FirebaseService firebaseService;
//    private final EmailSenderService emailSenderService;
//    private final TelegramService telegramService;
//    private final OneSignalService oneSignalService;
//    private final PushyService pushyService;
//    @RabbitListener(queues = "notification_queue",autoStartup = "true",ackMode = "MANUAL")
//    public void consumeNotification(PushyMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
////        oneSignalService.sendNotification(message);
//        pushyService.sendPushNotification(message);
//        channel.basicAck(tag,false);
//    }
//
//    @RabbitListener(queues = "email_queue",autoStartup = "true",ackMode = "MANUAL")
//    public void consumeEmail(Email message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws MessagingException, IOException {
//        log.info("Email: " + message);
//        emailSenderService.sendHtmlMessage(message);
//        channel.basicAck(tag,false);
//    }
//
//    @RabbitListener(queues = "sms_queue",autoStartup = "true",ackMode = "MANUAL")
//    public void consumeSms(TwilioMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException  {
//        log.info("SMS: " + message);
//        channel.basicAck(tag,false);
//    }
//
//    @RabbitListener(queues = "telegram_queue",autoStartup = "true",ackMode = "MANUAL")
//    public void consumeTelegram(TelegramMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
//        log.info("Telegram: " + message);
//        telegramService.sendMessage(message);
//        channel.basicAck(tag,false);
//    }
//
//    @RabbitListener(queues = "whatsapp_queue",autoStartup = "true",ackMode = "MANUAL")
//    public void consumeWhatsapp(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
//        log.info("Whatsapp: " + message);
//        channel.basicAck(tag,false);
//    }
//}
