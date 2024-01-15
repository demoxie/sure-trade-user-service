package org.saultech.suretradeuserservice.payment.enums;

public enum PaymentType {
  TRANSACTION("TRANSACTION"),
    SUBSCRIPTION("SUBSCRIPTION"),
    PROMOTION("PROMOTION"),
    COMMISSION("COMMISSION");
  
  private final String value;
  
    PaymentType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
}
