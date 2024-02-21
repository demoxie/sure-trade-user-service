package org.saultech.suretradeuserservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import static org.saultech.suretradeuserservice.constants.BaseRoutes.ADMINS;
import org.saultech.suretradeuserservice.user.service.AdminService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = ADMINS)
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public Mono<APIResponse> getUsers() {
        return adminService.getUsers()
                .collectList()
                .map(users-> APIResponse.builder()
                        .message("Users retrieved successfully")
                        .statusCode(200)
                        .data(users)
                        .build());
    }

    @GetMapping("/merchants")
    public Mono<APIResponse> getMerchants() {
        return adminService.getMerchants()
                .collectList()
                .map(merchants-> APIResponse.builder()
                        .message("Merchants retrieved successfully")
                        .statusCode(200)
                        .data(merchants)
                        .build());
    }


    @GetMapping("/admins")
    public Mono<APIResponse> getAdmins() {
        return adminService.getAdmins()
                .collectList()
                .map(admins-> APIResponse.builder()
                        .message("Admins retrieved successfully")
                        .statusCode(200)
                        .data(admins)
                        .build());
    }

    @GetMapping("/super-admins")
    public Mono<APIResponse> getSuperAdmins() {
        return adminService.getSuperAdmins()
                .collectList()
                .map(superAdmins-> APIResponse.builder()
                        .message("Super Admins retrieved successfully")
                        .statusCode(200)
                        .data(superAdmins)
                        .build());
    }

    @GetMapping("/merchants/requests")
    public Mono<APIResponse> getMerchantRequests() {
        return adminService.getMerchantRequests()
                .collectList()
                .map(merchantRequests-> APIResponse.builder()
                        .message("Merchant requests retrieved successfully")
                        .statusCode(200)
                        .data(merchantRequests)
                        .build());
    }

    @GetMapping("/merchants/requests/get-by-id/{id}")
    public Mono<APIResponse> getMerchantRequestById(@PathVariable long id) {
        return adminService.getMerchantRequestById(id)
                .map(merchantRequest-> APIResponse.builder()
                        .message("Merchant request retrieved successfully")
                        .statusCode(200)
                        .data(merchantRequest)
                        .build());
    }

    @PutMapping("/merchants/requests/{id}/approve")
    public Mono<APIResponse> approveMerchantRequest(@PathVariable long id) {
        return adminService.approveMerchantRequest(id)
                .map(merchantRequest-> APIResponse.builder()
                        .message("Merchant request approved successfully")
                        .statusCode(200)
                        .data(merchantRequest)
                        .build());
    }
}
