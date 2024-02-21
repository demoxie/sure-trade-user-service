package org.saultech.suretradeuserservice.user.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BecomeMerchantRequestsVO {
    private Long userId;
    private String userWalletAddress;
    private String transactionHashId;
    private BigDecimal amount;
    private String currency;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private String country;
    private String status;
}
