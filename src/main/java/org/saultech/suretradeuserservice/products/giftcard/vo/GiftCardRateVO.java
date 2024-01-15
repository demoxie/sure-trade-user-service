package org.saultech.suretradeuserservice.products.giftcard.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftCardRateVO implements java.io.Serializable{
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

    @JsonProperty("giftCardType")
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
