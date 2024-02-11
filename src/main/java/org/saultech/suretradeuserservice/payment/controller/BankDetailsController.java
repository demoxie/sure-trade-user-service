package org.saultech.suretradeuserservice.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.payment.dto.BankDetailsDto;
import org.saultech.suretradeuserservice.payment.service.BankDetailsService;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.saultech.suretradeuserservice.constants.BaseRoutes.BANK_DETAILS;

@RestController
@RequestMapping(BANK_DETAILS)
@RequiredArgsConstructor
@Validated
public class BankDetailsController {
    private final BankDetailsService paymentService;

    @PostMapping("/")
    public Mono<APIResponse> addBankDetails(@Valid @RequestBody BankDetailsDto bankDetailsDto) {
        LoggingService.logRequest(bankDetailsDto,"User Service", "/", "POST");
        return paymentService.addBankDetails(bankDetailsDto);
    }

    @GetMapping("/")
    public Mono<APIResponse> getAllBankDetails() {
        LoggingService.logRequest("","User Service", "/", "GET");
        return paymentService.getAllBankDetails();
    }

    @GetMapping("/{userId}")
    public Mono<APIResponse> getBankDetailsByUserId(@PathVariable long userId) {
        LoggingService.logRequest(userId,"User Service", "/", "GET");
        return paymentService.getBankDetailsByUserId(userId);
    }

    @GetMapping("/mine/all")
    public Mono<APIResponse> getAllMyBankDetails() {
        LoggingService.logRequest("","User Service", "/", "GET");
        return paymentService.getMyBankDetails();
    }

    @GetMapping("/account-numbers/{accountNumber}")
    public Mono<APIResponse> getBankDetailsByAccountNumber(@PathVariable String accountNumber) {
        LoggingService.logRequest(accountNumber,"User Service", "/", "GET");
        return paymentService.getBankDetailsByAccountNumber(accountNumber);
    }

    @PutMapping("/{id}")
    public Mono<APIResponse> updateBankDetails(@PathVariable long id, @Valid @RequestBody BankDetailsDto bankDetailsDto) {
        LoggingService.logRequest(bankDetailsDto,"User Service", "/", "PUT");
        return paymentService.updateBankDetails(id, bankDetailsDto);
    }

    @DeleteMapping("/{id}")
    public Mono<APIResponse> deleteBankDetails(@PathVariable long id) {
        LoggingService.logRequest(id,"User Service", "/", "DELETE");
        return paymentService.deleteBankDetails(id);
    }


}
