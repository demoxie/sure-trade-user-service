package org.saultech.suretradeuserservice.business.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.business.rating.dto.RatingDto;
import org.saultech.suretradeuserservice.business.rating.entity.Rating;
import org.saultech.suretradeuserservice.business.rating.vo.RatingVO;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.business.rating.repository.RatingRepository;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;
import org.saultech.suretradeuserservice.utils.ErrorUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final RatingRepository ratingRepository;

    @Override
    public Mono<APIResponse> rate(RatingDto ratingDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .doOnNext(username -> log.info("Username: {}", username))
                .flatMap(userRepository::findUsersByEmail)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("User not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Error getting user", throwable);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(throwable))
                                    .statusCode(ErrorUtils.getStatusCode(throwable))
                                    .build()
                    );
                })
                .flatMap(user -> ratingRepository
                        .findByUserIdAndRaterId(ratingDto.getUserId(), user.getId())
                        .flatMap(rating -> Mono.error(
                                APIException.builder()
                                        .message("Rating already exists")
                                        .statusCode(400)
                                        .build()
                        ))
                        .switchIfEmpty(Mono.defer(() -> {
                            Rating rating = mapper.map(ratingDto, Rating.class);
                            rating.setRaterId(user.getId());
                            rating.setCreatedAt(LocalDateTime.now());
                            return ratingRepository.save(rating)
                                    .onErrorResume(throwable -> {
                                        log.error("Error saving rating", throwable);
                                        return Mono.error(
                                                APIException.builder()
                                                        .message(ErrorUtils.getErrorMessage(throwable))
                                                        .statusCode(ErrorUtils.getStatusCode(throwable))
                                                        .build()
                                        );
                                    });
                        }))
                )
                .map(rating -> {
                    log.info("Rating saved: {}", rating);
                    return APIResponse.builder()
                            .message("Rating saved")
                            .statusCode(200)
                            .data(rating)
                            .build();
                });
    }

    @Override
    public Mono<APIResponse> getUserRating(long userId) {
        return ratingRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .message("Rating not found")
                                .statusCode(404)
                                .build()
                ))
                .onErrorResume(throwable -> {
                    log.error("Error getting rating", throwable);
                    return Mono.error(
                            APIException.builder()
                                    .message(ErrorUtils.getErrorMessage(throwable))
                                    .statusCode(ErrorUtils.getStatusCode(throwable))
                                    .build()
                    );
                })
                .collectList()
                .flatMap(rating -> {
                    List<Long> raterIds = rating.stream()
                            .map(Rating::getRaterId)
                            .toList();
                    return userRepository.findAllById(raterIds)
                            .map(user -> mapper.map(user, UserProfileVO.class))
                            .collectList()
                            .flatMap(raterList -> {
                                double totalRating = rating.stream()
                                        .mapToDouble(Rating::getRating)
                                        .sum();
                                int numberOfRatings = rating.size();
                                double averageRating = totalRating / numberOfRatings;
                                RatingVO ratingVO = RatingVO.builder()
                                        .rating(averageRating)
                                        .ratingCount(numberOfRatings)
                                        .raters(raterList)
                                        .build();
                                return Mono.just(ratingVO);
                            })
                            .flatMap(ratingVO -> {
                                log.info("Rating found: {}", ratingVO);
                                return Mono.just(APIResponse.builder()
                                        .message("Rating found")
                                        .statusCode(200)
                                        .data(ratingVO)
                                        .build());
                            });

                });
    }
}
