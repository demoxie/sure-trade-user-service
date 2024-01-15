package org.saultech.suretradeuserservice.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotEmpty(message = "Current password is required")
    private String oldPassword;
    @NotEmpty(message = "New password is required")
    private String newPassword;
    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;
}
