package org.saultech.suretradeuserservice.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StakeWithdrawalDto {
    private String paymentMethod;
    private Long walletAddressId;
    private String paymentCurrency;
    private BigDecimal amount;
    private String reasonForWithdrawal;
    private Long stakedAssetId;
}
