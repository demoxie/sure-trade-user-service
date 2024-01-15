package org.saultech.suretradeuserservice.config.app;

import lombok.Data;

@Data
public class GiftCardTransaction {
    private String queue;
    private String exchange;
    private String routingKey;
}
