package org.saultech.suretradeuserservice.user.service;

import io.lettuce.core.Value;
import org.saultech.suretradeuserservice.user.entity.BecomeMerchantRequests;
import org.saultech.suretradeuserservice.user.vo.BecomeMerchantRequestsVO;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminService {
    Flux<UserProfileVO> getUsers();

    Flux<UserProfileVO> getMerchants();

    Flux<UserProfileVO> getAdmins();

    Flux<UserProfileVO> getSuperAdmins();

    Flux<BecomeMerchantRequestsVO> getMerchantRequests();

    Mono<BecomeMerchantRequestsVO> getMerchantRequestById(long id);

    Mono<BecomeMerchantRequestsVO> approveMerchantRequest(long id);
}
