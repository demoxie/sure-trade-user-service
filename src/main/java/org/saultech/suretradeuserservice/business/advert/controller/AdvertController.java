package org.saultech.suretradeuserservice.business.advert.controller;

import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.business.advert.dto.AdvertDto;
import org.saultech.suretradeuserservice.business.advert.service.AdvertService;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/advertisements")
@RequiredArgsConstructor
public class AdvertController {
    private final AdvertService advertService;

    @PostMapping("/")
    public Mono<APIResponse> createAdvert(@RequestBody AdvertDto advertDto) {
        return advertService.createAdvert(advertDto);
    }

    @GetMapping("/")
    public Mono<APIResponse> getAdverts() {
        return advertService.getAdverts();
    }

    @GetMapping("/{id}")
    public Mono<APIResponse> getAdvertById(@PathVariable long id) {
        return advertService.getAdvertById(id);
    }

    @PutMapping("/{id}")
    public Mono<APIResponse> updateAdvert(@PathVariable long id, @RequestBody AdvertDto advertDto) {
        return advertService.updateAdvert(id, advertDto);
    }

    @DeleteMapping("/{id}")
    public Mono<APIResponse> deleteAdvert(@PathVariable long id) {
        return advertService.deleteAdvert(id);
    }


}
