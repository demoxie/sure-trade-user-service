package org.saultech.suretradeuserservice.payment.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    BANK_TRANSFER("Bank Transfer"),
    USSD("USSD"),
    QR_CODE("QR Code"),
    CASH("Cash"),
    BITCOIN("Bitcoin"),
    BINANCE_COIN("Binance Coin");
    private final String method;

    PaymentMethod(String method){
        this.method = method;
    }

}
