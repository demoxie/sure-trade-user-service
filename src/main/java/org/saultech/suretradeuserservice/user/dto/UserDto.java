package org.saultech.suretradeuserservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.user.enums.Gender;
import org.saultech.suretradeuserservice.user.enums.Role;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto (
     String firstName,

     String lastName,

     String middleName,

     String username,

     String token,

     String otp,

     String transactionPin,

     Role roles,

     String walletAddress,

     String nonce,

     String phoneNumber,

     String telegram,

     String profilePicture,

     String address,

     String city,

     String state,

     String country,

     Gender gender,

     String email,

     String password,

     Long tierId,

     Long transactionProfileId,

     String referralCodes,

     String telegramChatId,

     boolean isActive,

     boolean isSuspended,

     boolean isVerified
){}
