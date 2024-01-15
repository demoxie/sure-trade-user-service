package org.saultech.suretradeuserservice.products.giftcard.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    BUY("BUY"),
    SELL("SELL");

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

}
