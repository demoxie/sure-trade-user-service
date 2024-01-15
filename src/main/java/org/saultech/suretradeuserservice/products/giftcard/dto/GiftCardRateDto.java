package org.saultech.suretradeuserservice.products.giftcard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftCardRateDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("cardName")
    private String cardName;
    @JsonProperty("merchantId")
    private Long merchantId;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("giftCardCurrency")
    private String giftCardCurrency;
    @JsonProperty("transactionType")
    private String transactionType;
    @JsonProperty("paymentMethod")
    private String paymentMethod;
    @JsonProperty("bankDetailsId")
    private Long bankDetailsId;
    @JsonProperty("walletAddressId")
    private Long walletAddressId;
    @JsonProperty("maxLimit")
    private BigDecimal maxLimit;
    @JsonProperty("minLimit")
    private BigDecimal minLimit;
    @JsonProperty("rate")
    private BigDecimal rate;
    @JsonProperty("status")
    private String status;
}
