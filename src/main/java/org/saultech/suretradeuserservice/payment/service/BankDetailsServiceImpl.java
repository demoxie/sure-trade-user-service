package org.saultech.suretradeuserservice.payment.service;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheEvict;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCachePut;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.payment.dto.BankDetailsDto;
import org.saultech.suretradeuserservice.payment.entity.BankDetails;
import org.saultech.suretradeuserservice.payment.repository.BankDetailsRepository;
import org.saultech.suretradeuserservice.payment.vo.BankDetailsVO;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.utils.ErrorUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankDetailsServiceImpl implements BankDetailsService{
    private final BankDetailsRepository bankDetailsRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    @Override
    public Mono<APIResponse> addBankDetails(BankDetailsDto bankDetailsDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> bankDetailsRepository.findAllByAccountNumber(bankDetailsDto.getAccountNumber())
                        .onErrorResume(ex -> Mono.error(
                                APIException.builder()
                                        .message(ErrorUtils.getErrorMessage(ex))
                                        .statusCode(ErrorUtils.getStatusCode(ex))
                                        .build()
                        ))
                        .flatMap(bankDetails -> {
                            if (bankDetails != null) {
                                return Mono.error(
                                        APIException.builder()
                                                .message("Bank details already exist")
                                                .statusCode(400)
                                                .build()
                                );
                            }
                            return Mono.empty();
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            BankDetails bankDetails = mapper.map(bankDetailsDto, BankDetails.class);
                            bankDetails.setUserId(user.getId());
                            bankDetails.setCreatedAt(LocalDateTime.now());
                            bankDetails.setUpdatedAt(LocalDateTime.now());
                            return bankDetailsRepository.save(bankDetails)
                                    .onErrorResume(ex -> Mono.error(
                                            APIException.builder()
                                                    .message(ErrorUtils.getErrorMessage(ex))
                                                    .statusCode(ErrorUtils.getStatusCode(ex))
                                                    .build()
                                    ))
                                    .switchIfEmpty(Mono.error(
                                            APIException.builder()
                                                    .message("Bank details not added")
                                                    .statusCode(500)
                                                    .build()
                                    ));
                        })))
                .flatMap(bankDetails -> {
                    BankDetailsVO bankDetailsVO = mapper.map(bankDetails, BankDetailsVO.class);
                    return Mono.just(APIResponse.builder()
                            .data(bankDetailsVO)
                            .message("Bank details added successfully")
                            .statusCode(201)
                            .build());
                });
    }

    @Override
    public Mono<APIResponse> getBankDetails(long userId) {
        return bankDetailsRepository.findAllByUserId(userId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Bank details not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(ex -> Mono.error(
                        APIException.builder()
                                .message(ErrorUtils.getErrorMessage(ex))
                                .statusCode(ErrorUtils.getStatusCode(ex))
                                .build()
                ))
                .map(bankDetails -> mapper.map(bankDetails, BankDetailsVO.class))
                .collectList()
                .map(bankDetails -> APIResponse.builder()
                        .data(bankDetails)
                        .message("Bank details retrieved successfully")
                        .statusCode(200)
                        .build());
    }

    @Override
    public Mono<APIResponse> updateBankDetails(long id, BankDetailsDto bankDetailsDto) {
        return bankDetailsRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Bank details not found")
                                .statusCode(404)
                                .build()
                ))
                .flatMap(bankDetails -> {
                    bankDetails.setAccountNumber(bankDetailsDto.getAccountNumber());
                    bankDetails.setBankName(bankDetailsDto.getBankName());
                    bankDetails.setAccountName(bankDetailsDto.getAccountName());
                    bankDetails.setUpdatedAt(LocalDateTime.now());
                    return bankDetailsRepository.save(bankDetails)
                            .onErrorResume(ex -> Mono.error(
                                    APIException.builder()
                                            .message(ErrorUtils.getErrorMessage(ex))
                                            .statusCode(ErrorUtils.getStatusCode(ex))
                                            .build()
                            ))
                            .switchIfEmpty(Mono.error(
                                    APIException.builder()
                                            .message("Bank details not updated")
                                            .statusCode(500)
                                            .build()
                            ));
                })
                .flatMap(bankDetails -> {
                    BankDetailsVO bankDetailsVO = mapper.map(bankDetails, BankDetailsVO.class);
                    return Mono.just(APIResponse.builder()
                            .data(bankDetailsVO)
                            .message("Bank details updated successfully")
                            .statusCode(200)
                            .build());
                });
    }

    @Override
    public Mono<APIResponse> deleteBankDetails(long id) {
        return bankDetailsRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Bank details not found")
                                .statusCode(404)
                                .build()
                ))
                .flatMap(bankDetails -> bankDetailsRepository.delete(bankDetails)
                        .onErrorResume(ex -> Mono.error(
                                APIException.builder()
                                        .message(ErrorUtils.getErrorMessage(ex))
                                        .statusCode(ErrorUtils.getStatusCode(ex))
                                        .build()
                        ))
                        .then(Mono.just(APIResponse.builder()
                                .message("Bank details deleted successfully")
                                .statusCode(200)
                                .build()))
                );
    }

    @Override
    public Mono<APIResponse> getBankDetailsByAccountNumber(String accountNumber) {
        return bankDetailsRepository.findAllByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Bank details not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(ex -> Mono.error(
                        APIException.builder()
                                .message(ErrorUtils.getErrorMessage(ex))
                                .statusCode(ErrorUtils.getStatusCode(ex))
                                .build()
                ))
                .map(bankDetails -> mapper.map(bankDetails, BankDetailsVO.class))
                .flatMap(bankDetails -> Mono.just(APIResponse.builder()
                        .data(bankDetails)
                        .message("Bank details retrieved successfully")
                        .statusCode(200)
                        .build()));
    }

    @Override
    public Mono<APIResponse> getMyBankDetails() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> bankDetailsRepository.findAllByUserId(user.getId())
                        .switchIfEmpty(Mono.error(
                                APIException.builder()
                                        .message("Bank details not found")
                                        .statusCode(404)
                                        .build()
                        ))
                        .onErrorResume(ex -> Mono.error(
                                APIException.builder()
                                        .message(ErrorUtils.getErrorMessage(ex))
                                        .statusCode(ErrorUtils.getStatusCode(ex))
                                        .build()
                        ))
                        .map(bankDetails -> mapper.map(bankDetails, BankDetailsVO.class))
                        .collectList()
                        .flatMap(bankDetails -> Mono.just(APIResponse.builder()
                                .data(bankDetails)
                                .message("Bank details retrieved successfully")
                                .statusCode(200)
                                .build()))
                );
    }

    @Override
    public Mono<APIResponse> getAllBankDetails() {
        return bankDetailsRepository.findAllBy()
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Bank details not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(ex -> Mono.error(
                        APIException.builder()
                                .message(ErrorUtils.getErrorMessage(ex))
                                .statusCode(ErrorUtils.getStatusCode(ex))
                                .build()
                ))
                .map(bankDetails -> mapper.map(bankDetails, BankDetailsVO.class))
                .collectList()
                .map(bankDetails -> APIResponse.builder()
                        .data(bankDetails)
                        .message("Bank details retrieved successfully")
                        .statusCode(200)
                        .build());
    }

    @Override
    public Mono<APIResponse> getBankDetailsByUserId(long userId) {
        return bankDetailsRepository.findAllByUserId(userId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Bank details not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(ex -> {
                    log.error("Error occurred while retrieving bank details: {}", ex.getMessage());
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(ex))
                                    .statusCode(ErrorUtils.getStatusCode(ex))
                                    .build()
                    );
                })
                .map(bankDetails -> mapper.map(bankDetails, BankDetailsVO.class))
                .collectList()
                .flatMap(bankDetails -> Mono.just(APIResponse.builder()
                        .data(bankDetails)
                        .message("Bank details retrieved successfully")
                        .statusCode(200)
                        .build()));
    }
}
