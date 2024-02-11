package org.saultech.suretradeuserservice.business.referals.entity;


import lombok.*;
import org.saultech.suretradeuserservice.business.referals.enums.ReferralType;
import org.saultech.suretradeuserservice.user.entity.BaseEntity;
import org.saultech.suretradeuserservice.business.referals.enums.ReferralStatus;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("Referrals")
public class Referral extends BaseEntity {
    @Column("referrer")
    public Long referrer;
    @Column("referee")
    public Long referee;
    @Column("referralCode")
    public String referralCode;
    @Column("referralLink")
    public String referralLink;
    @Column("referralType")
    public ReferralType referralType;
    @Column("referralValue")
    public BigDecimal referralValue;
    @Column("expiryDate")
    public LocalDate expiryDate;
    @Column("status")
    public ReferralStatus status;
}
