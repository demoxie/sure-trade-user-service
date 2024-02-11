package org.saultech.suretradeuserservice.business.advert.repository;

import org.saultech.suretradeuserservice.business.advert.entity.Advert;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AdvertRepository extends R2dbcRepository<Advert, Long> {
    Mono<Advert> findByTitle(String title);

    Flux<Advert> findAllBy();
}
