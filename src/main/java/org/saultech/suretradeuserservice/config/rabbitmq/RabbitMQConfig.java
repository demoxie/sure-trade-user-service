package org.saultech.suretradeuserservice.config.rabbitmq;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.saultech.suretradeuserservice.config.app.AppConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConfig {
    private final AppConfig appConfig;
    private final RabbitmqPropertyConfig rabbitmqPropertyConfig;
    @Bean
    public Queue notificationQueue() {
        return new Queue(appConfig.getNotification().getQueue(), true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(appConfig.getEmail().getQueue(), true);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(appConfig.getSms().getQueue(), true);
    }

    @Bean
    public Queue telegramQueue() {
        return new Queue(appConfig.getTelegram().getQueue(), true);
    }

    @Bean
    public Queue telegramActivation() {
        return new Queue(appConfig.getTelegramActivation().getQueue(), true);
    }

    @Bean
    public Queue whatsappQueue() {
        return new Queue(appConfig.getWhatsapp().getQueue(), true);
    }

    @Bean
    public Queue giftCardTransactionQueue() {
        return new Queue(appConfig.getGiftCardTransaction().getQueue(), false);
    }

    //Create dead letter queue
    @Bean
    public Queue notificationDeadLetterQueue() {
        return QueueBuilder.durable(appConfig.getNotification().getDeadLetterQueue())
                .withArgument("x-dead-letter-exchange", appConfig.getNotification().getExchange())
                .withArgument("x-dead-letter-routing-key", appConfig.getNotification().getRoutingKey())
                .build();
    }

    @Bean
    public Queue emailDeadLetterQueue() {
        return QueueBuilder.durable(appConfig.getEmail().getDeadLetterQueue())
                .withArgument("x-dead-letter-exchange", appConfig.getEmail().getExchange())
                .withArgument("x-dead-letter-routing-key", appConfig.getEmail().getRoutingKey())
                .build();
    }

    @Bean
    public Queue smsDeadLetterQueue() {
        return QueueBuilder.durable(appConfig.getSms().getDeadLetterQueue())
                .withArgument("x-dead-letter-exchange", appConfig.getSms().getExchange())
                .withArgument("x-dead-letter-routing-key", appConfig.getSms().getRoutingKey())
                .build();
    }

    @Bean
    public Queue telegramDeadLetterQueue() {
        return QueueBuilder.durable(appConfig.getTelegram().getDeadLetterQueue())
                .withArgument("x-dead-letter-exchange", appConfig.getTelegram().getExchange())
                .withArgument("x-dead-letter-routing-key", appConfig.getTelegram().getRoutingKey())
                .build();
    }

    @Bean
    public Queue whatsappDeadLetterQueue() {
        return QueueBuilder.durable(appConfig.getWhatsapp().getDeadLetterQueue())
                .withArgument("x-dead-letter-exchange", appConfig.getWhatsapp().getExchange())
                .withArgument("x-dead-letter-routing-key", appConfig.getWhatsapp().getRoutingKey())
                .build();
    }

    @Bean
    public Exchange notificationExchange() {
        return new DirectExchange(appConfig.getNotification().getExchange());
    }

    @Bean
    public Exchange emailExchange() {
        return new DirectExchange(appConfig.getEmail().getExchange());
    }

    @Bean
    public Exchange smsExchange() {
        return new DirectExchange(appConfig.getSms().getExchange());
    }

    @Bean
    public Exchange telegramExchange() {
        return new DirectExchange(appConfig.getTelegram().getExchange());
    }

    @Bean
    public Exchange telegramActivationExchange() {
        return new DirectExchange(appConfig.getTelegramActivation().getExchange());
    }

    @Bean
    public Exchange whatsappExchange() {
        return new DirectExchange(appConfig.getWhatsapp().getExchange());
    }


    @Bean
    public Exchange giftCardTransactionExchange() {
        return new DirectExchange(appConfig.getGiftCardTransaction().getExchange());
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with(appConfig.getNotification().getRoutingKey()).noargs();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(appConfig.getEmail().getRoutingKey()).noargs();
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(smsExchange()).with(appConfig.getSms().getRoutingKey()).noargs();
    }

    @Bean
    public Binding telegramBinding() {
        return BindingBuilder.bind(telegramQueue()).to(telegramExchange()).with(appConfig.getTelegram().getRoutingKey()).noargs();
    }

    @Bean
    public Binding telegramActivationBinding() {
        return BindingBuilder.bind(telegramActivation()).to(telegramActivationExchange()).with(appConfig.getTelegramActivation().getRoutingKey()).noargs();
    }

    @Bean
    public Binding whatsappBinding() {
        return BindingBuilder.bind(whatsappQueue()).to(whatsappExchange()).with(appConfig.getWhatsapp().getRoutingKey()).noargs();
    }

    @Bean
    public Binding giftCardTransactionBinding() {
        return BindingBuilder.bind(giftCardTransactionQueue()).to(giftCardTransactionExchange()).with(appConfig.getGiftCardTransaction().getRoutingKey()).noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Primary
    public RabbitTemplate rabbitmqTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitmqTemplate = new RabbitTemplate();
        rabbitmqTemplate.setConnectionFactory(connectionFactory);
        rabbitmqTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitmqTemplate;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
