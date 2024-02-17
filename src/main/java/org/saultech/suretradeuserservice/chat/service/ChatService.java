package org.saultech.suretradeuserservice.chat.service;

import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import org.saultech.suretradeuserservice.chat.dto.ChatDto;
import org.saultech.suretradeuserservice.chat.vo.ChatVO;
import org.saultech.suretradeuserservice.common.APIResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatService {
    Mono<ChatVO> sendMessage(ChatDto chatDto);

    Mono<Void> deleteMessage(Long chatId);

    Mono<ChatVO> markAsRead(Long chatId);

    Flux<ChatVO> getUserChatHistory(Long userId, int page, int size, String sort, String direction);

    Flux<ChatVO> getTransactionChatHistory(Long userId, Long chatId, Long transactionId, int page, int size, String sort, String direction);

    Flux<ChatVO> getChatHistoryBetween(Long userId, Long transactionId, int page, int size, String sort, String direction);

    Flux<ChatVO> getMyChatHistory(long userId, long transactionId, int page, int size, String sort, String direction);

    Flux<ChatVO> getUnreadMessages(long transactionId, int page, int size, String sort, String direction);

    Flux<ChatVO> getUnreadMessagesBetween(long userId, long transactionId, int page, int size, String sort, String direction);

    Mono<ChatVO> getChatById(Long chatId);
}
