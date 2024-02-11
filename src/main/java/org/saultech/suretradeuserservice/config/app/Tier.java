package org.saultech.suretradeuserservice.config.app;

import lombok.Data;

@Data
public class Tier {
    private long id;
    private Double minStake;
    private Double maxStake;
    private Integer noOfReferrals;
    private Double referralBonus;
    private Integer noOfTransactions;
}
