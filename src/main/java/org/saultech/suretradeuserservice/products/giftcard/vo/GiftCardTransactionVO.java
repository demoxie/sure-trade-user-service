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
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftCardTransactionVO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("giftCardId")
    private Long giftCardId;
    @JsonProperty("giftCardRateId")
    private Long giftCardRateId;
    @JsonProperty("giftCardRate")
    private GiftCardRateVO giftCardRate;
    @JsonProperty("bankDetails")
    private BankDetailsVO bankDetails;
    @JsonProperty("user")
    private UserProfileVO user;
    @JsonProperty("merchant")
    private UserProfileVO merchant;
    @JsonProperty("giftCard")
    private GiftCardVO giftCard;
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("merchantId")
    private Long merchantId;
    @JsonProperty("transactionType")
    private String transactionType;
    @JsonProperty("cardType")
    private String cardType;
    @JsonProperty("cardIssuer")
    private String cardIssuer;
    @JsonProperty("paymentMethod")
    private String paymentMethod;

    @JsonProperty("referenceNo")
    private String referenceNo;

    @JsonProperty("bankDetailsId")
    private Long bankDetailsId;
    @JsonProperty("walletAddressId")
    private Long walletAddressId;
    @JsonProperty("paymentId")
    private Long paymentId;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("fee")
    private BigDecimal fee;
    @JsonProperty("currency")
    private String currency;
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
