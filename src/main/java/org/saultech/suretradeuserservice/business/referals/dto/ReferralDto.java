package org.saultech.suretradeuserservice.business.referals.dto;

import lombok.Data;
import org.saultech.suretradeuserservice.business.referals.enums.ReferralType;

@Data
public class ReferralDto {
    private Long id;
    private Long referrerId;
    private Long refereeId;
    private ReferralType referralType;
    private String createdDate;
    private String updatedDate;
    private String referralStatus;
}
