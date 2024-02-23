package org.saultech.suretradeuserservice.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.auth.AuthRequest;
import org.saultech.suretradeuserservice.auth.JwtService;
import org.saultech.suretradeuserservice.auth.dto.ChangePasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.ResetPasswordRequest;
import org.saultech.suretradeuserservice.auth.dto.SignUpRequest;
import org.saultech.suretradeuserservice.auth.dto.VerifyOtpRequest;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.config.app.*;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.messaging.email.Email;
import org.saultech.suretradeuserservice.messaging.sms.Sms;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.enums.Role;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.saultech.suretradeuserservice.utils.ErrorUtils;
import org.saultech.suretradeuserservice.utils.UtilService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.data.relational.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Producer producer;
    private final ObjectMapper objectMapper;
    private final BusinessConfig businessConfig;


    @Override
    public Mono<UserProfileVO> login(@Valid @RequestBody AuthRequest authRequest) {
        AtomicReference<User> userToSave = new AtomicReference<>();
        return userRepository.findUsersByEmail(authRequest.getEmail())
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Invalid username or password")
                                .statusCode(401)
                                .build()
                ))
                .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Invalid username or password")
                                .statusCode(401)
                                .build()
                ))
                .map(user -> {
                    userToSave.set(user);
                    return user;
                })
                .filter(User::isVerified)
                .switchIfEmpty(Mono.defer(()->{
                    Duration expirationTime = Duration.ofMinutes(15);
                    String otp = UtilService.generate6DigitOTP(6);
                    User user = userToSave.get();
                    user.setOtp(otp);
                    redisTemplate.opsForValue().getAndDelete(authRequest.getEmail());
                    redisTemplate.opsForValue().set(authRequest.getEmail(), otp, expirationTime);
                    Email email = Email.builder()
                            .to(authRequest.getEmail())
                            .subject("Account Verification")
                            .body(Map.of("otp", otp))
                            .template("account-verification")
                            .createdDate(new Date())
                            .build();
                    producer.sendEmail(email);
                    Sms sms = Sms.builder()
                            .to(user.getPhoneNumber())
                            .body("Your OTP is " + otp)
                            .build();
                    producer.sendSms(sms);
                    r2dbcEntityTemplate.update(User.class)
                            .matching(Query.query(where("id").is(user.getId())))
                            .apply(
                     Update.update("otp", otp)
                            .set("isActive", true)
                            .set("updatedAt", LocalDateTime.now())
                    ).subscribe();
                    return Mono.error(
                            APIException.builder()
                                    .message("Another OTP sent for verification")
                                    .statusCode(200)
                                    .build()
                    );
                }))
                .filter(users -> !users.isSuspended())
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Account suspended")
                                .statusCode(401)
                                .build()
                ))
                .flatMap(user -> {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("username", user.getUsername());
                    claims.put("email", user.getEmail());
                    claims.put("role", List.of(user.getRoles()));
                    claims.put("id", user.getId());
                    String token = jwtService.generateToken(claims, user.getEmail());
                    user.setToken(token);
                    user.setActive(true);
                    user.setUpdatedAt(LocalDateTime.now());
                    return r2dbcEntityTemplate.update(User.class)
                            .matching(Query.query(where("id").is(user.getId())))
                            .apply(
                                    getUpdate(token)
                            )
                            .flatMap(existingUser -> {
                                if (existingUser == 1) {
                                    ReactiveSecurityContextHolder.withAuthentication(
                                            new UsernamePasswordAuthenticationToken(
                                                    user.getEmail(),
                                                    null,
                                                    user.getAuthorities()
                                            )
                                    );
                                    return userRepository.findUsersByEmail(user.getEmail())
                                            .map(user1 -> {
                                                UserProfileVO userProfileVO =  modelMapper.map(user1, UserProfileVO.class);
                                                userProfileVO.setCreatedAt(user1.getCreatedAt());
                                                userProfileVO.setUpdatedAt(user1.getUpdatedAt());
                                                return userProfileVO;
                                            });
                                }
                                return Mono.error(
                                        APIException.builder()
                                                .message("Unable to update user")
                                                .statusCode(500)
                                                .build()
                                );
                            });
                });
    }

    private static Update getUpdate(String token) {
        return Update.update("token", token)
                .set("isActive", true)
                .set("updatedAt", LocalDateTime.now());
    }

    @Override
    public Mono<UserProfileVO> register(SignUpRequest request) {
        return userRepository.findUsersByEmail(request.getEmail())
                .onErrorResume(ex->{
                    log.error("Error occurred: {}", ex.getClass());
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(ex))
                                    .statusCode(ErrorUtils.getStatusCode(ex))
                                    .build()
                    );
                })
                .flatMap(user -> {
                    if (Objects.nonNull(user)) {
                        return Mono.error(
                                APIException.builder()
                                        .message("Account already exists")
                                        .statusCode(409)
                                        .build()
                        );
                    }
                    return Mono.just(new UserProfileVO());
                })
                .switchIfEmpty(handleRegistration(request));


    }

    public Mono<UserProfileVO> handleRegistration(SignUpRequest request){
        Business business = businessConfig.getBusiness();
        Tiers tiers = business.getTiers();
        Tier tier1 = tiers.getTier1();
        User userToSave = modelMapper.map(request, User.class);
        userToSave.setPassword(passwordEncoder.encode(request.getPassword()));
        userToSave.setCreatedAt(LocalDateTime.now());
        userToSave.setUpdatedAt(LocalDateTime.now());
        userToSave.setRoles(Role.USER);
        userToSave.setTierId(tier1.getId());
        String otp = UtilService.generate6DigitOTP(6);
        userToSave.setOtp(otp);
        return userRepository.save(userToSave)
                .onErrorResume(ex->{
                    log.error("Error occurred: {}", ex.getClass());

                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(ex))
                                    .statusCode(ErrorUtils.getStatusCode(ex))
                                    .build()
                    );
                })
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Unable to save user")
                                .statusCode(500)
                                .build()
                ))
                .map(user -> {
                    log.info("User saved: {}", user);
                    Duration expirationTime = Duration.ofMinutes(15);
                    redisTemplate.opsForValue().set(user.getEmail(), otp, expirationTime);
                    Email email = Email.builder()
                            .to(user.getEmail())
                            .subject("Account Verification")
                            .body(Map.of("otp", otp))
                            .template("account-verification")
                            .createdDate(new Date())
                            .build();

                    producer.sendEmail(email);
                    Sms sms = Sms.builder()
                            .to(user.getPhoneNumber())
                            .body("Your OTP is " + otp)
                            .build();

                    producer.sendSms(sms);
                    ReactiveSecurityContextHolder.withAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    null,
                                    user.getAuthorities()
                            )
                    );
                    return modelMapper.map(user, UserProfileVO.class);
                });
    }

    @Override
    public Mono<APIResponse> verifyOtp(VerifyOtpRequest request) {

        return userRepository.findUsersByOtp(request.getOtp())
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("User not found")
                                .statusCode(404)
                                .build()
                ))
                .flatMap(user -> {
                    String otp = (String) redisTemplate.opsForValue().get(user.getEmail());
                    if (otp == null) {
                        Email email = Email.builder()
                                .to(user.getEmail())
                                .subject("Account Verification")
                                .body(Map.of("otp", user.getOtp()))
                                .template("account-verification")
                                .createdDate(new Date())
                                .build();
                        producer.sendEmail(email);
                        Sms sms = Sms.builder()
                                .to(user.getPhoneNumber())
                                .body("Your OTP is " + user.getOtp())
                                .build();

                        producer.sendSms(sms);

                        return Mono.error(
                                APIException.builder()
                                        .message("OTP used or has expired")
                                        .statusCode(400)
                                        .build()
                        );
                    }
                    if (!otp.equals(request.getOtp())) {
                        return Mono.error(
                                APIException.builder()
                                        .message("Invalid OTP")
                                        .statusCode(400)
                                        .build()
                        );
                    }
                    log.info("OTP is valid");
                    redisTemplate.delete(user.getEmail());
                    user.setOtp(null);
                    user.setActive(true);
                    user.setVerified(true);
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user)
                            .onErrorResume(ex->{
                                log.error("Error occurred: {}", ex.getClass());
                                return Mono.error(
                                        APIException.builder()
                                                .message(ErrorUtils.getErrorMessage(ex))
                                                .statusCode(ErrorUtils.getStatusCode(ex))
                                                .build()
                                );
                            })
                            .flatMap(successState -> {
                                    ReactiveSecurityContextHolder.withAuthentication(
                                            new UsernamePasswordAuthenticationToken(
                                                    user.getEmail(),
                                                    null,
                                                    user.getAuthorities()
                                            )
                                    );
                                    Email email = Email.builder()
                                            .to(user.getEmail())
                                            .subject("Account Verification Successful")
                                            .body(Map.of("username", user.getUsername()))
                                            .template("account-verification-success")
                                            .createdDate(new Date())
                                            .build();

                                    producer.sendEmail(email);
                                    var result = modelMapper.map(user, UserProfileVO.class);
                                    return Mono.just(
                                            APIResponse.builder()
                                                    .message("Account verified successfully")
                                                    .statusCode(200)
                                                    .data(result)
                                                    .build()
                                    );
                            });
                });
    }

    @Override
    public Mono<Void> logout(String token) {
        if (token == null) return Mono.empty();
        token = token.replace("Bearer ", "");
        String finalToken = token;
        return Mono.just(token)
                .map(jwtService::getAllClaimsFromToken)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Invalid token")
                                .statusCode(401)
                                .build()
                ))
                .filter(user -> !jwtService.isTokenExpired(finalToken))
                .flatMap(claims -> {
                    String email = (String) claims.get("email");
                    return userRepository.findUsersByEmail(email)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("User not found")
                                            .statusCode(404)
                                            .build()
                            ))
                            .filter(user -> StringUtils.isNotEmpty(user.getToken())&& user.getToken().equals(finalToken))
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("Invalid token")
                                            .statusCode(401)
                                            .build()
                            ))
                            .filter(User::isActive)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("User is logged out already")
                                            .statusCode(200)
                                            .build()
                            ))
                            .flatMap(user -> {
                                user.setToken(null);
                                user.setActive(false);
                                user.setUpdatedAt(LocalDateTime.now());
                                return r2dbcEntityTemplate.update(user.getClass())
                                        .matching(Query.query(where("id").is(user.getId())))
                                        .apply(
                                                Update.update("token", null)
                                                        .set("isActive", false)
                                                        .set("updatedAt", LocalDateTime.now())
                                        )
                                        .filter(successState -> successState == 1)
                                        .switchIfEmpty(Mono.error(
                                                APIException.builder()
                                                        .message("Unable to update user")
                                                        .statusCode(500)
                                                        .build()
                                        ))
                                        .flatMap(successState -> {
                                            ReactiveSecurityContextHolder.clearContext();
                                            return Mono.empty();
                                        });
                            });
                });
    }

    @Override
    public Mono<Void> changePassword(ChangePasswordRequest request, String userAgent, String ip) {
        log.info("Changing password from device: {} and {}", userAgent, ip);
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("User not found")
                                .statusCode(404)
                                .build()
                ))
                .filter(user -> Objects.equals(request.getNewPassword(), request.getConfirmPassword()))
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("New password and confirm password do not match")
                                .statusCode(400)
                                .build()
                ))
                .filter(user -> passwordEncoder.matches(request.getOldPassword(),user.getPassword()))
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Invalid old password")
                                .statusCode(400)
                                .build()
                ))
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    user.setUpdatedAt(LocalDateTime.now());
                    return r2dbcEntityTemplate.update(user.getClass())
                            .matching(Query.query(where("id").is(user.getId())))
                            .apply(
                                    Update.update("password", user.getPassword())
                                            .set("updatedAt", LocalDateTime.now())
                            )
                            .filter(successState -> successState == 1)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("Unable to update user")
                                            .statusCode(500)
                                            .build()
                            ))
                            .flatMap(successState -> {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                                LocalDateTime localDateTime = LocalDateTime.now();

                                String formattedDateTime = localDateTime.format(formatter);
                                Email email = Email.builder()
                                        .to(user.getEmail())
                                        .subject("Password Change")
                                        .body(Map.of(
                                                "username", user.getUsername(),
                                                "passwordChangeTime", formattedDateTime,
                                                "userAgent", userAgent,
                                                "ipAddress", ip
                                        ))
                                        .template("password-change")
                                        .createdDate(new Date())
                                        .build();

                                producer.sendEmail(email);
                                Sms sms = Sms.builder()
                                        .to(user.getPhoneNumber())
                                        .body("Your password was changed at " + LocalDateTime.now() + " from " + userAgent + " with IP " + ip)
                                        .build();

                                producer.sendSms(sms);
                                return Mono.empty();
                            });
                });
    }

    @Override
    public Mono<Void> resetPassword(String otp, ResetPasswordRequest request, String userAgent, String ip) {
        return userRepository.findUsersByOtp(otp)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("User not found")
                                .statusCode(404)
                                .build()
                ))
                .filter(user -> user.getOtp().equals(otp))
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Invalid OTP")
                                .statusCode(400)
                                .build()
                ))
                .flatMap(user -> {
                    String otp1 = (String) redisTemplate.opsForValue().get(user.getEmail());
                    if (otp1 == null) {
                        log.info("OTP used or has expired");
                        return Mono.error(
                                APIException.builder()
                                        .message("OTP used or has expired")
                                        .statusCode(400)
                                        .build()
                        );
                    }
                    if (!otp1.equals(otp)) {
                        return Mono.error(
                                APIException.builder()
                                        .message("Invalid OTP")
                                        .statusCode(400)
                                        .build()
                        );
                    }
                    log.info("OTP is valid");
                    redisTemplate.delete(user.getEmail());
                    user.setOtp(null);
                    user.setUpdatedAt(LocalDateTime.now());
                    return r2dbcEntityTemplate.update(user.getClass())
                            .matching(Query.query(where("id").is(user.getId())))
                            .apply(
                                    Update.update("otp", null)
                                            .set("updatedAt", LocalDateTime.now())
                            )
                            .filter(successState -> successState == 1)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("Unable to update user")
                                            .statusCode(500)
                                            .build()
                            ))
                            .flatMap(successState -> {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                                LocalDateTime localDateTime = LocalDateTime.now();

                                String formattedDateTime = localDateTime.format(formatter);
                                Email email = Email.builder()
                                        .to(user.getEmail())
                                        .subject("Password Reset")
                                        .body(Map.of(
                                                "username", user.getUsername(),
                                                "passwordResetTime", formattedDateTime,
                                                "userAgent", userAgent,
                                                "ipAddress", ip
                                        ))
                                        .template("password-change")
                                        .createdDate(new Date())
                                        .build();

                                producer.sendEmail(email);

                                Sms sms = Sms.builder()
                                        .to(user.getPhoneNumber())
                                        .body("Your password was reset at " + LocalDateTime.now() + " from " + userAgent + " with IP " + ip)
                                        .build();

                                producer.sendSms(sms);
                                return Mono.empty();
                            });
                });
    }

    @Override
    public Mono<Void> validateAccountForPasswordChange(String email) {
        return userRepository.findUsersByEmail(email)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Account doesn't exist")
                                .statusCode(404)
                                .build()
                ))
                .filter(user->!user.isSuspended())
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Account suspended")
                                .statusCode(401)
                                .build()
                ))
                .filter(User::isVerified)
                .switchIfEmpty(
                        Mono.error(
                                APIException.builder()
                                        .message("Account not verified")
                                        .statusCode(401)
                                        .build()
                        )
                )
                .flatMap(user->{
                    if (!user.isVerified()){
                        String otp = UtilService.generate6DigitOTP(6);
                        return r2dbcEntityTemplate.update(User.class)
                                .matching(Query.query(where("email").is(email)))
                                .apply(
                                        Update.update("otp", otp)
                                                .set("updatedAt", LocalDateTime.now())
                                )
                                .filter(successState -> successState == 1)
                                .switchIfEmpty(Mono.error(
                                        APIException.builder()
                                                .message("Unable to update user")
                                                .statusCode(500)
                                                .build()
                                ))
                                .flatMap(successState -> {
                                    Duration expirationTime = Duration.ofMinutes(15);
                                    redisTemplate.opsForValue().set(email, otp, expirationTime);
                                    Email email1 = Email.builder()
                                            .to(email)
                                            .subject("Password Reset")
                                            .body(Map.of(
                                                    "otp", otp,
                                                    "username", user.getUsername()
                                            ))
                                            .template("password-reset")
                                            .createdDate(new Date())
                                            .build();

                                    producer.sendEmail(email1);
                                    Sms sms = Sms.builder()
                                            .to(user.getPhoneNumber())
                                            .body("Your OTP is " + otp)
                                            .build();

                                    producer.sendSms(sms);
                                    return Mono.empty();
                                });
                    }

                    String otp = UtilService.generate6DigitOTP(6);
                    return r2dbcEntityTemplate.update(User.class)
                            .matching(Query.query(where("email").is(email)))
                            .apply(
                                    Update.update("otp", otp)
                                            .set("updatedAt", LocalDateTime.now())
                            )
                            .filter(successState -> successState == 1)
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("Unable to update user")
                                            .statusCode(500)
                                            .build()
                            ))
                            .flatMap(successState -> {
                                Duration expirationTime = Duration.ofMinutes(15);
                                redisTemplate.opsForValue().set(email, otp, expirationTime);
                                Email email1 = Email.builder()
                                        .to(email)
                                        .subject("Password Reset")
                                        .body(Map.of("otp", otp))
                                        .template("password-reset")
                                        .createdDate(new Date())
                                        .build();

                                producer.sendEmail(email1);
                                Sms sms = Sms.builder()
                                        .to(user.getPhoneNumber())
                                        .body("Your OTP is " + otp)
                                        .build();

                                producer.sendSms(sms);
                                return Mono.empty();
                            });

                });
    }
}
