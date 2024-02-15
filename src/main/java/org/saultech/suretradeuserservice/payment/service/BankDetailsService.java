package org.saultech.suretradeuserservice.payment.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.payment.dto.BankDetailsDto;
import reactor.core.publisher.Mono;

public interface BankDetailsService {
    Mono<APIResponse> addBankDetails(BankDetailsDto bankDetailsDto);

    Mono<APIResponse> getBankDetails(long userId);

    Mono<APIResponse> updateBankDetails(long id, BankDetailsDto bankDetailsDto);

    Mono<APIResponse> deleteBankDetails(long id);

    Mono<APIResponse> getBankDetailsByAccountNumber(String accountNumber);

    Mono<APIResponse> getMyBankDetails();

    Mono<APIResponse> getAllBankDetails();

    Mono<APIResponse> getBankDetailsByUserId(long userId);
}
