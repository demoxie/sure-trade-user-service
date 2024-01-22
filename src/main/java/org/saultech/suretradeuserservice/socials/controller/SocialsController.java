package org.saultech.suretradeuserservice.socials.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.socials.service.SocialsService;
import org.saultech.suretradeuserservice.user.dto.RegisterTelegramDto;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import static org.saultech.suretradeuserservice.constants.BaseRoutes.SOCIALS;

@RestController
@RequestMapping(value = SOCIALS, produces = "application/json")
@RequiredArgsConstructor
@Slf4j
public class SocialsController {
    private final SocialsService socialsService;

    @PostMapping("/telegram/register")
    public Mono<APIResponse> registerTelegram(@Valid @RequestBody RegisterTelegramDto dto) {
        LoggingService.logRequest(dto, "User Service","/users/telegram/register", "POST");
        return socialsService.registerTelegram(dto);
    }
}
