package org.saultech.suretradeuserservice.business.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.business.rating.dto.RatingDto;
import org.saultech.suretradeuserservice.business.rating.service.RatingService;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/create")
    public Mono<APIResponse> rate(@Valid @RequestBody RatingDto ratingDto) {
        LoggingService.logRequest(ratingDto, "User Service", "/ratings/rate", "POST");
        return ratingService.rate(ratingDto);
    }

    @GetMapping("/get")
    public Mono<APIResponse> getUserRating(
            @RequestParam(name = "userId") long userId
    ) {
        return ratingService.getUserRating(userId);
    }
}
