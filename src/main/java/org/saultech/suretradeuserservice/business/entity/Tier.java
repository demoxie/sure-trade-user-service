package org.saultech.suretradeuserservice.business.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.user.entity.BaseEntity;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "Tiers")
//@RedisHash("Tiers")
public class Tier extends BaseEntity {
    private String tierName;
    private String tierDescription;
    private String stakedAmountRange;
    private Integer noOfTransactions;
    private Integer noOfReferrals;
    private Integer referralBonus;
    private Integer referralBonusType;
    private Integer referralBonusAmount;
    private Integer referralBonusPercentage;
}
