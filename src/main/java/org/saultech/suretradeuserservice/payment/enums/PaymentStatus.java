package org.saultech.suretradeuserservice.payment.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    BUYER_PAID("Buyer Paid"),
    SELLER_PAID("Seller Paid"),
    BUYER_REFUNDED("Buyer Refunded"),
    SELLER_REFUNDED("Seller Refunded"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    PENDING("Pending"),
    FAILED("Failed");
    private final String status;

    PaymentStatus(String status){
        this.status = status;
    }

}
