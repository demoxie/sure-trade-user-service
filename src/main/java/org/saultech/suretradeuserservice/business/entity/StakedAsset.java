package org.saultech.suretradeuserservice.business.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.user.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
@Table(name = "StakedAssets")
public class StakedAsset extends BaseEntity {
    @Column("userId")
    private Long userId;
    @Column("tierId")
    private Long tierId;
    @Column("adminId")
    private Long adminId;
    @Column("transactionHashId")
    private String transactionHashId;
    @Column("userWalletAddress")
    private String userWalletAddress;
    @Column("adminWalletAddress")
    private String adminWalletAddress;
    @Column("currency")
    private String currency;
    @Column("amount")
    private BigDecimal amount;
    @Column("balance")
    private BigDecimal balance;
    @Column("previousBalance")
    private BigDecimal previousBalance;
    @Column("status")
    private String status;
}
