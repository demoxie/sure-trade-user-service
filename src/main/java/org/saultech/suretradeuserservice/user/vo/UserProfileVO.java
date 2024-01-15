package org.saultech.suretradeuserservice.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.common.BaseVO;
import org.saultech.suretradeuserservice.user.enums.Gender;
import org.saultech.suretradeuserservice.user.enums.Role;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileVO extends BaseVO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("username")
    private String username;

    @JsonProperty("token")
    private String token;

    @JsonProperty("otp")
    private String otp;

    @JsonProperty("transactionPin")
    private String transactionPin;

    @JsonProperty("roles")
    private Role roles;

    @JsonProperty("walletAddress")
    private String walletAddress;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("telegram")
    private String telegram;

    @JsonProperty("profilePicture")
    private String profilePicture;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("email")
    private String email;

    @JsonProperty("tierId")
    private Long tierId;

    @JsonProperty("transactionProfileId")
    private Long transactionProfileId;

    @JsonProperty("referralCode")
    private String referralCodes;

    @JsonProperty("referralCodeId")
    private String telegramChatId;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("isSuspended")
    private boolean isSuspended;

    @JsonProperty("isVerified")
    private boolean isVerified;
}
