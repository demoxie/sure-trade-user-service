package org.saultech.suretradeuserservice.products.giftcard.enums;

public enum TransactionStatus {
    NEW("NEW"),
    PROCESSING("PROCESSING");

    private final String status;

    TransactionStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
