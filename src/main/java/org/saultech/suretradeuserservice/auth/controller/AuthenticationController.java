package org.saultech.suretradeuserservice.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.auth.AuthRequest;
import org.saultech.suretradeuserservice.auth.dto.ResetPasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.VerifyOtpRequest;
import org.saultech.suretradeuserservice.auth.service.AuthenticationService;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.saultech.suretradeuserservice.auth.dto.ChangePasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.SignUpRequest;
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
        return authenticationService.login(authRequest);
    }

    @PostMapping("/register")
    public Mono<UserProfileVO> register(@Valid @RequestBody SignUpRequest request){
        return authenticationService.register(request);
    }

    @PostMapping("/verify-otp")
    public Mono<UserProfileVO> verifyOtp(@Valid @RequestBody VerifyOtpRequest request){
        return authenticationService.verifyOtp(request);
    }

    @PostMapping("/logout/me")
    public Mono<String> logout(@RequestHeader("Authorization") String token){
        return authenticationService.logout(token)
                .then(Mono.defer(() -> Mono.just("Logout successful")));
    }

    @PutMapping("/change-password/mine")
    public Mono<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, ServerWebExchange exchange){
        //Get UserAgent from request
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        return authenticationService.changePassword(request, userAgent, ip)
                .then(Mono.defer(() -> Mono.just("Password changed successfully")));
    }

    @PutMapping("/reset-password/{otp}")
    public Mono<String> resetPassword(@PathVariable String otp, @Valid @RequestBody ResetPasswordRequest request, ServerWebExchange exchange){
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        return authenticationService.resetPassword(otp, request, userAgent, ip)
                .then(Mono.defer(() -> Mono.just("Password reset successfully")));
    }

    @GetMapping("/validate/{email}")
    public Mono<String> validateAccountForPasswordReset(@PathVariable String email){
        return authenticationService.validateAccountForPasswordChange(email)
                .then(Mono.defer(() -> Mono.just("Password reset Code sent successfully")));
    }
}
