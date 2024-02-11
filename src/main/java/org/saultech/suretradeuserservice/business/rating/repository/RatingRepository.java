package org.saultech.suretradeuserservice.business.rating.repository;

import org.saultech.suretradeuserservice.business.rating.entity.Rating;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RatingRepository extends R2dbcRepository<Rating, Long> {
    Flux<Rating> findByUserId(Long userId);
    Mono<Rating> findByRaterId(Long raterId);

    Mono<Rating> findByUserIdAndRaterId(Long userId, Long raterId);

}
