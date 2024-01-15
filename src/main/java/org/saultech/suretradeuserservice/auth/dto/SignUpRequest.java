package org.saultech.suretradeuserservice.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.saultech.suretradeuserservice.user.enums.Gender;
import org.saultech.suretradeuserservice.user.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpRequest {
    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;
    private String middleName;
    @NotEmpty(message = "Username is required")
    private String username;

    private String phoneNumber;

    private String profilePicture;

    private String address;


    private String city;

    private String state;

    @NotEmpty(message = "Country is required")
    private String country;

    private Gender gender;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid", regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;

    @NotEmpty(message = "Password is required")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter and one number")
    private String password;
}
