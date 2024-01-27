package org.saultech.suretradeuserservice.products.giftcard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.saultech.suretradeuserservice.products.giftcard.enums.TransactionType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateGiftCardTransactionDto {
    @NotEmpty(message = "Card type is required")
    @JsonProperty("cardType")
    private String cardType;

    @NotEmpty(message = "Card issuer is required")
    @JsonProperty("cardIssuer")
    private String cardIssuer;

    @NotNull(message = "Gift card rate id is required")
    @JsonProperty("giftCardRateId")
    private Long giftCardRateId;

    @JsonProperty("giftCardId")
    private Long giftCardId;

    @NotNull(message = "Transaction type is required")
    @JsonProperty("transactionType")
    private TransactionType transactionType;

    @NotNull(message = "Merchant id is required")
    @JsonProperty("merchantId")
    private Long merchantId;

    @NotEmpty(message = "Payment method is required")
    @JsonProperty("paymentMethod")
    private String paymentMethod;

    @NotNull(message = "Bank details id is required")
    @JsonProperty("bankDetailsId")
    private Long bankDetailsId;

    @JsonProperty("referenceNo")
    private String referenceNo;

    @NotNull(message = "Amount is required")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotNull(message = "Quantity is required")
    @JsonProperty("quantity")
    private Integer quantity;

    @NotEmpty(message = "Currency is required")
    @JsonProperty("currency")
    private String currency;

//    @NotNull(message = "Gift card dto is required")
    @JsonProperty("giftCardDto")
    private GiftCardDto giftCardDto;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("fee")
    private BigDecimal fee;

    @JsonProperty("status")
    private String status;
}
