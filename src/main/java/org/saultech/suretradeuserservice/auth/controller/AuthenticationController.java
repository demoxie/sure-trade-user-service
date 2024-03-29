package org.saultech.suretradeuserservice.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.auth.AuthRequest;
import org.saultech.suretradeuserservice.auth.dto.ResetPasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.VerifyOtpRequest;
import org.saultech.suretradeuserservice.auth.service.AuthenticationService;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.saultech.suretradeuserservice.auth.dto.ChangePasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.SignUpRequest;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping(value = "/auth", produces = "application/json", consumes = "application/json")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public Mono<UserProfileVO> login(@Valid  @RequestBody AuthRequest authRequest){
        LoggingService.logRequest(authRequest, "User Service", "/auth/login", "POST");
        return authenticationService.login(authRequest);
    }

    @PostMapping("/register")
    public Mono<UserProfileVO> register(@Valid @RequestBody SignUpRequest request){
        LoggingService.logRequest(request, "User Service", "/auth/register", "POST");
        return authenticationService.register(request);
    }

    @PostMapping("/verify-otp")
    public Mono<APIResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request){
        LoggingService.logRequest(request, "User Service", "/auth/verify-otp", "POST");
        return authenticationService.verifyOtp(request);
    }

    @PostMapping("/logout/me")
    public Mono<String> logout(@RequestHeader("Authorization") String token){
        LoggingService.logRequest(token, "User Service", "/auth/logout/me", "POST");
        return authenticationService.logout(token)
                .then(Mono.defer(() -> Mono.just("Logout successful")));
    }

    @PutMapping("/change-password/mine")
    public Mono<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, ServerWebExchange exchange){
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        LoggingService.logRequest(request, "User Service", "/auth/change-password/mine", "PUT");
        return authenticationService.changePassword(request, userAgent, ip)
                .then(Mono.defer(() -> Mono.just("Password changed successfully")));
    }

    @PutMapping("/reset-password/{otp}")
    public Mono<String> resetPassword(@PathVariable String otp, @Valid @RequestBody ResetPasswordRequest request, ServerWebExchange exchange){
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        LoggingService.logRequest(request, "User Service", "/auth/reset-password/{otp}", "PUT");
        return authenticationService.resetPassword(otp, request, userAgent, ip)
                .then(Mono.defer(() -> Mono.just("Password reset successfully")));
    }

    @GetMapping("/validate/{email}")
    public Mono<String> validateAccountForPasswordReset(@PathVariable String email){
        LoggingService.logRequest(email, "User Service", "/auth/validate/{email}", "GET");
        return authenticationService.validateAccountForPasswordChange(email)
                .then(Mono.defer(() -> Mono.just("Password reset Code sent successfully")));
    }
}
