package org.saultech.suretradeuserservice.config.app;

import lombok.Data;

@Data
public class TelegramActivation {
    private String queue;
    private String exchange;
    private String routingKey;
    private String deadLetterQueue;
}
