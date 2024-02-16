package org.saultech.suretradeuserservice.config.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Business {
    private String googlePlayAppLink;
    private BigDecimal signUpReferralValue;
    private BigDecimal signUpReferralDiscount;
    private Integer signUpReferralExpiryDate;
    private Tiers tiers;
}
