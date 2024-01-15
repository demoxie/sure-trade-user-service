package org.saultech.suretradeuserservice.config.app;

import lombok.Data;

@Data
public class Email {
    private String queue;
    private String exchange;
    private String routingKey;
    private String deadLetterQueue;
}
