package org.saultech.suretradeuserservice.products.giftcard.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.common.BaseVO;

import java.io.Serializable;
import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StakedAssetVO extends BaseVO implements Serializable {
    private Long id;
    private Long userId;
    private Long tierId;
    private Long adminId;
    private String transactionHashId;
    private String userWalletAddress;
    private String adminWalletAddress;
    private String currency;
    private BigDecimal amount;
    private BigDecimal balance;
    private BigDecimal previousBalance;
    private String status;
}
