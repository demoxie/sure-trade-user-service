package org.saultech.suretradeuserservice.chat.service;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import org.saultech.suretradeuserservice.chat.dto.ChatDto;
import org.saultech.suretradeuserservice.common.APIResponse;
import reactor.core.publisher.Mono;

public interface ChatService {
    Mono<APIResponse> sendMessage(ChatDto chatDto);

    Mono<APIResponse> deleteMessage(Long chatId);

    Mono<APIResponse> markAsRead(Long chatId);

    Mono<APIResponse> getUserChatHistory(Long userId, int page, int size, String sort, String direction);

    Mono<APIResponse> getTransactionChatHistory(Long userId, Long chatId, Long transactionId, int page, int size, String sort, String direction);

    Mono<APIResponse> getChatHistoryBetween(Long userId, Long transactionId, int page, int size, String sort, String direction);

    Mono<APIResponse> getMyChatHistory(long userId, long transactionId, int page, int size, String sort, String direction);

    Mono<APIResponse> getUnreadMessages(long transactionId, int page, int size, String sort, String direction);

    Mono<APIResponse> getUnreadMessagesBetween(long userId, long transactionId, int page, int size, String sort, String direction);

    Mono<APIResponse> getChatById(Long chatId);
}
