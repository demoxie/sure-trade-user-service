package org.saultech.suretradeuserservice.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "BecomeMerchantRequests")
public class BecomeMerchantRequests extends BaseEntity{
    @Column("userId")
    @JsonProperty("userId")
    private Long userId;
    @Column("userWalletAddress")
    private String userWalletAddress;
    @Column("transactionHashId")
    private String transactionHashId;
    @Column("amount")
    private BigDecimal amount;
    @Column("currency")
    private String currency;
    @Column("firstName")
    private String firstName;
    @Column("lastName")
    private String lastName;
    @Column("email")
    private String email;
    @Column("phoneNumber")
    private String phoneNumber;
    @Column("username")
    private String username;
    @Column("country")
    private String country;
    @Column("status")
    private String status;
}
