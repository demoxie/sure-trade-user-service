package org.saultech.suretradeuserservice.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.payment.enums.PaymentMethod;
import org.saultech.suretradeuserservice.payment.enums.PaymentStatus;
import org.saultech.suretradeuserservice.payment.enums.PaymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {
    @JsonProperty("merchantId")
    private Long merchantId;
    @JsonProperty("transactionId")
    private Long transactionId;
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("paymentMethod")
    private PaymentMethod paymentMethod;
    @JsonProperty("paymentStatus")
    private PaymentStatus paymentStatus;
    @JsonProperty("bankDetailsId")
    private Long bankDetailsId;
    @JsonProperty("screenshots")
    private String screenshots;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("amount")
    private Long amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("paymentDate")
    private LocalDate paymentDate;
    @JsonProperty("paymentReference")
    private String paymentReference;
    @JsonProperty("productType")
    private String productType;
    @JsonProperty("paymentType")
    private PaymentType paymentType;
    @JsonProperty("paymentDescription")
    private String paymentDescription;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
