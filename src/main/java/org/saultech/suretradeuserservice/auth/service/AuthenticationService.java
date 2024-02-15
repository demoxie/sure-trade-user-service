package org.saultech.suretradeuserservice.auth.service;

import jakarta.validation.Valid;
import org.saultech.suretradeuserservice.auth.AuthRequest;
import org.saultech.suretradeuserservice.auth.dto.ChangePasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.ResetPasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.SignUpRequest;
import org.saultech.suretradeuserservice.auth.dto.VerifyOtpRequest;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<UserProfileVO> login(@Valid AuthRequest authRequest);

    Mono<UserProfileVO> register(SignUpRequest request);

    Mono<APIResponse> verifyOtp(VerifyOtpRequest request);

    Mono<Void> logout(String token);

    Mono<Void> changePassword(ChangePasswordRequest request, String userAgent, String ip);

    Mono<Void> resetPassword(String otp, ResetPasswordRequest request, String userAgent, String ip);

    Mono<Void> validateAccountForPasswordChange(String email);
}
