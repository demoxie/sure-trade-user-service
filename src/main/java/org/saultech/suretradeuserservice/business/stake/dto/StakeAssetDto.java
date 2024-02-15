package org.saultech.suretradeuserservice.business.stake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StakeAssetDto {
    private String userWalletAddress;
    private String adminWalletAddress;
    private BigDecimal amount;
    private String currency;
    private String transactionHashId;
}
