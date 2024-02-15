package org.saultech.suretradeuserservice.business.referals.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.saultech.suretradeuserservice.business.referals.enums.ReferralType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralVO {
    private Long id;
    private Long referrer;
    private Long referee;
    private String referralCode;
    private ReferralType referralType;
    private String createdDate;
    private String updatedDate;
}
