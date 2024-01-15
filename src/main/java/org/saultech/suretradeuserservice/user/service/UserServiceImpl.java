package org.saultech.suretradeuserservice.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.auth.JwtService;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.messaging.email.Email;
import org.saultech.suretradeuserservice.messaging.sms.Sms;
import org.saultech.suretradeuserservice.products.giftcard.service.APIClientService;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.dto.BecomeAMerchantDto;
import org.saultech.suretradeuserservice.user.dto.ProfileImageDto;
import org.saultech.suretradeuserservice.user.dto.UserDto;
import org.saultech.suretradeuserservice.user.entity.BecomeMerchantRequests;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.enums.Role;
import org.saultech.suretradeuserservice.user.repository.BecomeMerchantRequestRepository;
import org.saultech.suretradeuserservice.business.repository.StakedAssetRepository;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;
    @Value("${otp.expiration}")
    private Integer otpExpiration;
    @Value("${app.admin.wallet-address}")
    private String adminWalletAddress;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.business.gift-card-transactions.fee}")
    private Double giftCardTransactionFee;
    private final JwtService jwtService;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Producer producer;
    private final ObjectMapper objectMapper;
    private final APIClientService apiClientService;
    private final BecomeMerchantRequestRepository becomeMerchantRequestRepository;
    private final StakedAssetRepository stakedAssetRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        return userRepository.findUsersByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(user -> {
                    log.info("User found: {}", user);
                    return org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .authorities(
                                    (GrantedAuthority) () -> user.getRoles().name()
                            )
                            .build();
                });
    }

    @Override
    public Flux<UserProfileVO> getUsers() {
        return userRepository.findAll()
                .map(user -> {
                    var userProfileVO = mapper.map(user, UserProfileVO.class);
                    userProfileVO.setCreatedAt(user.getCreatedAt());
                    userProfileVO.setUpdatedAt(user.getUpdatedAt());
                    return userProfileVO;
                });
    }

    @Override
    public Mono<UserProfileVO> getUserById(long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(user -> {
                    var userProfileVO = mapper.map(user, UserProfileVO.class);
                    userProfileVO.setCreatedAt(user.getCreatedAt());
                    userProfileVO.setUpdatedAt(user.getUpdatedAt());
                    return userProfileVO;
                });
    }

    @Override
    public Mono<User> getUserByUsername(String username) {
        return userRepository.findUsersByEmail(username);
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return userRepository.findUsersByEmail(email);
    }

    @Override
    public Mono<User> createUser(UserDto registerDto) {

        return userRepository.findUsersByEmail(registerDto.email())
                .flatMap(user -> {
                    if (user != null) {
                        return Mono.error(APIException.builder()
                                .message("User already exists")
                                .statusCode(409)
                                .build());
                    }
                    User newUser = mapper.map(registerDto, User.class);
                    newUser.setPassword(passwordEncoder.encode(registerDto.password()));
                    newUser.setCreatedAt(java.time.LocalDateTime.now());
                    newUser.setUpdatedAt(java.time.LocalDateTime.now());
                    return userRepository.save(newUser);
                });
    }

    @Override
    public Mono<UserProfileVO> getMe() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    log.info("Authentication: {}", authentication);
                    log.info("Principal: {}", authentication.getPrincipal());
                    var loggedInUser =  authentication.getPrincipal();
                    log.info("Logged in user: {}", loggedInUser);
                    return loggedInUser.toString();
                })
                .flatMap(userRepository::findUsersByEmail)
                .map(user -> {
                    var userProfileVO = mapper.map(user, UserProfileVO.class);
                    userProfileVO.setCreatedAt(user.getCreatedAt());
                    userProfileVO.setUpdatedAt(user.getUpdatedAt());
                    return userProfileVO;
                });
    }

    @Override
    public Mono<User> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    var loggedInUser =  authentication.getPrincipal();
                    return loggedInUser.toString();
                })
                .flatMap(userRepository::findUsersByEmail)
                .map(user -> user);
    }

    @Override
    public Mono<APIResponse> getMerchantRates(String cardName) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String username = securityContext.getAuthentication().getName();
                    log.info("Username: {}", username);
                    return getUserByUsername(username);
                })
                .flatMap(user -> apiClientService
                        .makeGetRequestWithQueryParamsAndFluxReturned("/gift-card/transaction/merchants/rates",
                                "product",
                                Map.of(
                                        "cardName", cardName,
                                        "userId", user.getId()
                                ),
                                "GiftCardRateVO"
                        ));
    }

    @Override
    public Mono<APIResponse> uploadProfileImage(ProfileImageDto dto) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String username = securityContext.getAuthentication().getName();
                    log.info("Username: {}", username);
                    return getUserByUsername(username);
                })
                .flatMap(user -> {
                    user.setProfilePicture(dto.getProfileImage());
                    return userRepository.save(user);
                })
                .map(user -> {
                    var userProfileVO = mapper.map(user, UserProfileVO.class);
                    userProfileVO.setCreatedAt(user.getCreatedAt());
                    userProfileVO.setUpdatedAt(user.getUpdatedAt());
                    return userProfileVO;
                })
                .map(user -> APIResponse.builder()
                        .message("Profile image uploaded successfully")
                        .statusCode(200)
                        .data(user)
                        .build());
    }

    @Override
    public Mono<APIResponse> getAdminWalletAddress() {
        return userRepository.findByRoles(Role.SUPER_ADMIN)
                .map(user -> APIResponse.builder()
                        .message("Admin address fetched successfully")
                        .statusCode(200)
                        .data(user.getWalletAddress())
                        .build());
    }

    @Override
    public Mono<APIResponse> becomeMerchant(BecomeAMerchantDto dto) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String username = securityContext.getAuthentication().getName();
                    return getUserByUsername(username);
                })
                .switchIfEmpty(Mono.error(APIException.builder()
                        .message("User not found")
                        .statusCode(404)
                        .build()))
                .flatMap(user -> {
                    if (user.getRoles().equals(Role.MERCHANT)) {
                        return Mono.error(APIException.builder()
                                .message("You are already a merchant")
                                .statusCode(409)
                                .build());
                    }
                    dto.setEmail(user.getEmail());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setUserId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setUsername(user.getUsername());
                    dto.setCountry(user.getCountry());
                    return Mono.just(dto);
                })
                .flatMap(request -> becomeMerchantRequestRepository.findByEmail(request.getEmail())
                        .switchIfEmpty(Mono.just(new BecomeMerchantRequests()))
                        .flatMap(merchantRequest -> {
                            if (merchantRequest != null && !Objects.isNull(merchantRequest.getStatus()) && merchantRequest.getStatus().equalsIgnoreCase("PROCESSING")) {
                                return Mono.error(APIException.builder()
                                        .message("You have already made a request to become a merchant")
                                        .statusCode(409)
                                        .build());
                            }
                            return handleMerchantRequest(request);
                        }));

    }

    private Mono<APIResponse> handleMerchantRequest(BecomeAMerchantDto dto) {
        return Mono.just(dto)
                .flatMap(merchantRequest -> {
                    BecomeMerchantRequests req = mapper.map(dto, BecomeMerchantRequests.class);
                    req.setStatus("PROCESSING");
                    req.setCreatedAt(java.time.LocalDateTime.now());
                    req.setUpdatedAt(java.time.LocalDateTime.now());
                    return stakedAssetRepository.findByUserId(dto.getUserId())
                            .switchIfEmpty(Mono.error(APIException.builder()
                                    .message("You have not staked any asset")
                                    .statusCode(409)
                                    .build()))
                            .flatMap(stakedAsset -> {
                                if (ObjectUtils.compare(stakedAsset.getBalance(), req.getAmount()) < 0) {
                                    return Mono.error(APIException.builder()
                                            .message("Insufficient staked asset")
                                            .statusCode(409)
                                            .build());
                                }
                                BigDecimal amountToBeDeducted = req.getAmount().multiply(BigDecimal.valueOf(giftCardTransactionFee));
                                stakedAsset.setPreviousBalance(stakedAsset.getBalance());
                                stakedAsset.setBalance(stakedAsset.getBalance().subtract(req.getAmount().add(amountToBeDeducted)));
                                stakedAsset.setAmount(req.getAmount());
                                stakedAsset.setAdminWalletAddress(adminWalletAddress);
                                stakedAsset.setTransactionHashId(req.getTransactionHashId());
                                stakedAsset.setCurrency(req.getCurrency());
                                stakedAsset.setStatus("UNCONFIRMED");
                                stakedAsset.setUserWalletAddress(req.getUserWalletAddress());
                                stakedAsset.setUpdatedAt(java.time.LocalDateTime.now());
                                return stakedAssetRepository.save(stakedAsset)
                                        .switchIfEmpty(Mono.error(APIException.builder()
                                                .message("An error occurred while processing your request")
                                                .statusCode(500)
                                                .build()))
                                        .flatMap(updatedStakedAsset->{
                                            return becomeMerchantRequestRepository.save(req)
                                                    .switchIfEmpty(
                                                            Mono.error(APIException.builder()
                                                                    .message("An error occurred while processing your request")
                                                                    .statusCode(500)
                                                                    .build())
                                                    ) .flatMap(Mono::just);
                                        })
                                        .flatMap(savedMerchantRequest -> {
                                            Map<String, Object> userEmailBody = Map.of(
                                                    "username", merchantRequest.getUsername()
                                            );
                                            Email toUser = Email.builder()
                                                    .to(merchantRequest.getEmail())
                                                    .subject("Request Sent Successfully")
                                                    .template("become-a-merchant-request-sent")
                                                    .body(userEmailBody)
                                                    .build();
                                            producer.sendEmail(toUser);

                                            Map<String, Object> adminEmailBody = Map.of(
                                                    "nameOfMerchantToBe", merchantRequest.getUsername(),
                                                    "username", adminUsername
                                            );

                                            Email emailToAdmin = Email.builder()
                                                    .to(adminEmail)
                                                    .subject("New Request Received")
                                                    .template("become-a-merchant-request")
                                                    .body(adminEmailBody)
                                                    .build();
                                            producer.sendEmail(emailToAdmin);
                                            Sms sms = Sms.builder()
                                                    .to(merchantRequest.getPhoneNumber())
                                                    .body("Your request to become a merchant is being processed. You will be contacted shortly")
                                                    .build();
                                            producer.sendSms(sms);
                                            return Mono.just(APIResponse.builder()
                                                    .message("Merchant request sent successfully")
                                                    .statusCode(200)
                                                    .data(merchantRequest)
                                                    .build());
                                        });
                            });

                });
    }


}
