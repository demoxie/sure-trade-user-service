package org.saultech.suretradeuserservice.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotEmpty(message = "OTP is required")
    private String otp;
}
