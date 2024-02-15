package org.saultech.suretradeuserservice.business.rating.service;

import org.saultech.suretradeuserservice.business.rating.dto.RatingDto;
import org.saultech.suretradeuserservice.common.APIResponse;
import reactor.core.publisher.Mono;

public interface RatingService {
    public Mono<APIResponse> rate(RatingDto ratingDto);

    Mono<APIResponse> getUserRating(long userId);
}
