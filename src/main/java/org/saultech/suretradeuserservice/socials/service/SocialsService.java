package org.saultech.suretradeuserservice.socials.service;

import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.user.dto.RegisterTelegramDto;
import reactor.core.publisher.Mono;

public interface SocialsService {
    Mono<APIResponse> registerTelegram(RegisterTelegramDto dto);
}
