package org.saultech.suretradeuserservice.payment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.common.BaseVO;
import org.saultech.suretradeuserservice.payment.dto.PaidTo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentVO extends BaseVO {
    private Long userId;
    private String transactionHashId;
    private String userTransactionHash;
    private String merchantTransactionHash;
    private Long merchantId;
    private Long transactionId;
    private BigDecimal amount;
    private String screenshots;
    private String currency;
    private String paymentMethod;
    private String paymentStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;
    private String paymentReference;
    private String paymentDescription;
    private String paymentType;
    private String productType;
    private Long bankDetailsId;
    private Long walletAddressId;
    private Long giftCardId;
    private String paidFor;
    private PaidTo paidTo;
}
