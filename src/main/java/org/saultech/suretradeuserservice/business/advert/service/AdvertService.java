package org.saultech.suretradeuserservice.business.advert.service;

import org.saultech.suretradeuserservice.business.advert.dto.AdvertDto;
import org.saultech.suretradeuserservice.common.APIResponse;
import reactor.core.publisher.Mono;

public interface AdvertService {
    Mono<APIResponse> createAdvert(AdvertDto advertDto);
    Mono<APIResponse> getAdverts();
    Mono<APIResponse> getAdvert(long advertId);
    Mono<APIResponse> deleteAdvert(long advertId);
    Mono<APIResponse> updateAdvert(long id, AdvertDto advertDto);

    Mono<APIResponse> getAdvertById(long id);
}
