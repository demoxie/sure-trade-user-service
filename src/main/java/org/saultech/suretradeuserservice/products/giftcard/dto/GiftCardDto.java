package org.saultech.suretradeuserservice.products.giftcard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftCardDto {
    private Long userId;
    private String cardCode;
    private String cardPin;
    @NotEmpty(message = "Card type is required")
    private String cardType;
    @NotEmpty(message = "Card issuer is required")
    private String cardIssuer;
    @NotEmpty(message = "Currency is required")
    private String currency;
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    @NotNull(message = "Card Value is required")
    private BigDecimal cardValue;

    private BigDecimal discount;

    @JsonProperty("expiryDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer.class)
    @JsonSerialize(using = com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer.class)
    private LocalDate expiryDate;

    private String status;

    @JsonProperty("screenshots")
    private Screenshots screenshots;
}
