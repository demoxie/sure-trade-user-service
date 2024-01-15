package org.saultech.suretradeuserservice.messaging;

public enum QueueExchange {
    EMAIL_EXCHANGE("email_exchange"),
    EMAIL_QUEUE("email_queue"),
    EMAIL_ROUTING_KEY("email_routing_key"),
    SMS_EXCHANGE("sms_exchange"),
    SMS_QUEUE("sms_queue"),
    SMS_ROUTING_KEY("sms_routing_key"),
    TELEGRAM_EXCHANGE("telegram_exchange"),
    TELEGRAM_QUEUE("telegram_queue"),
    TELEGRAM_ROUTING_KEY("telegram_routing_key"),
    TELEGRAM_ACTIVATION_EXCHANGE("telegram_activation_exchange"),
    TELEGRAM_ACTIVATION_QUEUE("telegram_activation_queue"),
    TELEGRAM_ACTIVATION_ROUTING_KEY("telegram_activation_routing_key"),
    WHATSAPP_EXCHANGE("whatsapp_exchange"),
    WHATSAPP_QUEUE("whatsapp_queue"),
    WHATSAPP_ROUTING_KEY("whatsapp_routing_key"),
    NOTIFICATION_EXCHANGE("notification_exchange"),
    NOTIFICATION_QUEUE("notification_queue"),
    NOTIFICATION_ROUTING_KEY("notification_routing_key"),
    NOTIFICATION_DEAD_LETTER_EXCHANGE("notification_dead_letter_exchange"),
    NOTIFICATION_DEAD_LETTER_QUEUE("notification_dead_letter_queue"),
    NOTIFICATION_DEAD_LETTER_ROUTING_KEY("notification_dead_letter_routing_key"),
    NOTIFICATION_DELAY_EXCHANGE("notification_delay_exchange"),
    NOTIFICATION_DELAY_QUEUE("notification_delay_queue"),
    NOTIFICATION_DELAY_ROUTING_KEY("notification_delay_routing_key"),
    NOTIFICATION_DELAY_DEAD_LETTER_EXCHANGE("notification_delay_dead_letter_exchange"),
    NOTIFICATION_DELAY_DEAD_LETTER_QUEUE("notification_delay_dead_letter_queue");

    private final String value;

    QueueExchange(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
