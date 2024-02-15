package org.saultech.suretradeuserservice.business.advert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.business.advert.dto.AdvertDto;
import org.saultech.suretradeuserservice.business.advert.entity.Advert;
import org.saultech.suretradeuserservice.business.advert.enums.AdvertStatus;
import org.saultech.suretradeuserservice.business.advert.repository.AdvertRepository;
import org.saultech.suretradeuserservice.business.advert.vo.AdvertVO;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.utils.ErrorUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertServiceImpl implements AdvertService{
    private final AdvertRepository advertRepository;
    private final ModelMapper mapper;
    @Override
    public Mono<APIResponse> createAdvert(AdvertDto advertDto) {
        Advert advert = Advert.builder()
                .title(advertDto.getTitle())
                .description(advertDto.getDescription())
                .imageUrls(advertDto.getImageUrls())
                .url(advertDto.getUrl())
                .status(AdvertStatus.ACTIVE.name())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return advertRepository.save(advert)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Error creating advert")
                                .statusCode(500)
                                .build()
                ))
                .onErrorResume(e -> {
                    log.error("Error creating advert", e);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(e))
                                    .statusCode(ErrorUtils.getStatusCode(e))
                                    .build()
                    );
                })
                .map(advert1 -> {
                    log.info("Advert created successfully");
                    return APIResponse.builder()
                            .message("Advert created successfully")
                            .statusCode(201)
                            .data(mapper.map(advert1, AdvertVO.class))
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> getAdverts() {
        return advertRepository.findAllBy()
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("No adverts found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(e -> {
                    log.error("Error getting adverts", e);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(e))
                                    .statusCode(ErrorUtils.getStatusCode(e))
                                    .build()
                    );
                })
                .collectList()
                .map(advertList -> {
                    log.info("Adverts retrieved successfully");
                    return APIResponse.builder()
                            .message("Adverts retrieved successfully")
                            .statusCode(200)
                            .data(advertList)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> getAdvert(long advertId) {
        return advertRepository.findById(advertId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Advert not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(e -> {
                    log.error("Error getting advert", e);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(e))
                                    .statusCode(ErrorUtils.getStatusCode(e))
                                    .build()
                    );
                })
                .map(advert -> {
                    log.info("Advert retrieved successfully");
                    return APIResponse.builder()
                            .message("Advert retrieved successfully")
                            .statusCode(200)
                            .data(mapper.map(advert, AdvertVO.class))
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> deleteAdvert(long advertId) {
        return advertRepository.findById(advertId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Advert not found")
                                .statusCode(404)
                                .build()
                ))
                .flatMap(advert -> advertRepository.delete(advert)
                        .then(Mono.just(advert)))
                .onErrorResume(e -> {
                    log.error("Error deleting advert", e);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(e))
                                    .statusCode(ErrorUtils.getStatusCode(e))
                                    .build()
                    );
                })
                .map(advert -> {
                    log.info("Advert deleted successfully");
                    return APIResponse.builder()
                            .message("Advert deleted successfully")
                            .statusCode(200)
                            .data(mapper.map(advert, AdvertVO.class))
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> updateAdvert(long id, AdvertDto advertDto) {
        return advertRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Advert not found")
                                .statusCode(404)
                                .build()
                ))
                .flatMap(advert -> {
                    advert.setTitle(advertDto.getTitle());
                    advert.setDescription(advertDto.getDescription());
                    advert.setImageUrls(advertDto.getImageUrls());
                    advert.setUrl(advertDto.getUrl());
                    advert.setUpdatedAt(LocalDateTime.now());
                    return advertRepository.save(advert);
                })
                .onErrorResume(e -> {
                    log.error("Error updating advert", e);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(e))
                                    .statusCode(ErrorUtils.getStatusCode(e))
                                    .build()
                    );
                })
                .map(advert -> {
                    log.info("Advert updated successfully");
                    return APIResponse.builder()
                            .message("Advert updated successfully")
                            .statusCode(200)
                            .data(mapper.map(advert, AdvertVO.class))
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> getAdvertById(long id) {
        return advertRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Advert not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(e -> {
                    log.error("Error getting advert", e);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(e))
                                    .statusCode(ErrorUtils.getStatusCode(e))
                                    .build()
                    );
                })
                .map(advert -> {
                    log.info("Advert retrieved successfully");
                    return APIResponse.builder()
                            .message("Advert retrieved successfully")
                            .statusCode(200)
                            .data(mapper.map(advert, AdvertVO.class))
                            .build();
                });
    }
}
