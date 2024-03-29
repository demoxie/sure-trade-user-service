package org.saultech.suretradeuserservice.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.chat.dto.ChatDto;
import org.saultech.suretradeuserservice.chat.dto.ChatScreenshots;
import org.saultech.suretradeuserservice.chat.entity.Chat;
import org.saultech.suretradeuserservice.chat.repository.ChatRepository;
import org.saultech.suretradeuserservice.chat.vo.ChatVO;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.messaging.notification.NotificationData;
import org.saultech.suretradeuserservice.messaging.notification.PushyMessage;
import org.saultech.suretradeuserservice.messaging.telegram.TelegramMessage;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.repository.UserDeviceDetailsRepository;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.utils.ErrorUtils;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService{
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserDeviceDetailsRepository userDeviceDetailsRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Producer producer;
    @Override
    public Mono<APIResponse> sendMessage(ChatDto chatDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> {
                    Chat newChat = mapper.map(chatDto, Chat.class);
                    JsonNode sct = objectMapper.convertValue(chatDto.getScreenshots(), JsonNode.class);
                    String screenshots = sct.toString();
                    newChat.setSenderId(user.getId());
                    newChat.setCreatedAt(LocalDateTime.now());
                    newChat.setUpdatedAt(LocalDateTime.now());
                    newChat.setScreenshots(screenshots);
                    return chatRepository.save(newChat)
                            .onErrorResume(e -> Mono.error(
                                    APIException.builder()
                                            .statusCode(ErrorUtils.getStatusCode(e))
                                            .message(ErrorUtils.getErrorMessage(e))
                                            .build()
                            ))
                            .flatMap(chat -> {
                                ChatVO chatVO = mapper.map(chat, ChatVO.class);
                                ChatScreenshots chatScreenshots = null;
                                try {
                                    chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                                chatVO.setScreenshots(chatScreenshots.getUrls());
                                return userRepository.findById(chat.getReceiverId())
                                        .switchIfEmpty(Mono.defer(
                                                Mono::empty
                                        ))
                                        .onErrorResume(e -> Mono.error(
                                                APIException.builder()
                                                        .statusCode(ErrorUtils.getStatusCode(e))
                                                        .message(ErrorUtils.getErrorMessage(e))
                                                        .build()
                                        ))
                                        .flatMap(receiver -> {
                                            TelegramMessage telegramMessage = TelegramMessage.builder()
                                                    .chatId(receiver.getTelegramChatId())
                                                    .message(chat.getMessage())
                                                    .build();
                                            producer.sendTelegram(telegramMessage);
                                            return userDeviceDetailsRepository.findByUserId(receiver.getId())
                                                    .switchIfEmpty(Mono.error(
                                                            APIException.builder()
                                                                    .statusCode(404)
                                                                    .message("Receiver device details not found")
                                                                    .build()
                                                    ))
                                                    .onErrorResume(e -> Mono.error(
                                                            APIException.builder()
                                                                    .statusCode(ErrorUtils.getStatusCode(e))
                                                                    .message(ErrorUtils.getErrorMessage(e))
                                                                    .build()
                                                    ))
                                                    .map(userDeviceDetails -> {
                                                        NotificationData notificationData = NotificationData.builder()
                                                                .message(chat.getMessage())
                                                                .build();
                                                        PushyMessage pushyMessage = PushyMessage.builder()
                                                                .to(userDeviceDetails.getDeviceToken())
                                                                .data(notificationData)
                                                                .build();
                                                        producer.sendNotification(pushyMessage);
                                                        return chatVO;
                                                    });
                                        })
                                        .map(chatVoResponse -> {
                                            LoggingService.logResponse(chatVoResponse, "User Service", "/chats/");
                                            return APIResponse.builder()
                                                    .statusCode(201)
                                                    .message("Message sent successfully")
                                                    .data(chatVoResponse)
                                                    .build();
                                        });
                            });
                });

    }

    @Override
    public Mono<APIResponse> deleteMessage(Long chatId) {
        return chatRepository.deleteById(chatId)
                .onErrorResume(e -> Mono.error(
                        APIException.builder()
                                .statusCode(ErrorUtils.getStatusCode(e))
                                .message(ErrorUtils.getErrorMessage(e))
                                .build()
                ))
                .map(chat -> APIResponse.builder()
                        .statusCode(200)
                        .message("Message deleted successfully")
                        .data(chat)
                        .build());
    }

    @Override
    public Mono<APIResponse> markAsRead(Long chatId) {
        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .statusCode(404)
                                .message("Chat not found")
                                .build()
                ))
                .flatMap(chat -> {
                    chat.setIsRead(true);
                    chat.setUpdatedAt(LocalDateTime.now());
                    return chatRepository.save(chat)
                            .onErrorResume(e -> Mono.error(
                                    APIException.builder()
                                            .statusCode(ErrorUtils.getStatusCode(e))
                                            .message(ErrorUtils.getErrorMessage(e))
                                            .build()
                            ))
                            .map(chats -> {
                                ChatVO chatVO =  mapper.map(chat, ChatVO.class);
                                ChatScreenshots chatScreenshots = null;
                                try {
                                    chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                                chatVO.setScreenshots(chatScreenshots.getUrls());
                                return chatVO;
                            })
                            .map(updatedChat -> APIResponse.builder()
                                    .statusCode(200)
                                    .message("Message marked as read")
                                    .data(updatedChat)
                                    .build());
                });
    }

    @Override
    public Mono<APIResponse> getUserChatHistory(Long userId, int page, int size, String sort, String direction) {
        Sort sortedBy = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sort);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sortedBy);
        return chatRepository.findBySenderId(userId, pageRequest)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .statusCode(404)
                                .message("No chat history found")
                                .build()
                ))
                .onErrorResume(e -> Mono.error(
                        APIException.builder()
                                .statusCode(ErrorUtils.getStatusCode(e))
                                .message(ErrorUtils.getErrorMessage(e))
                                .build()
                ))
                .map(chat -> {
                    ChatVO chatVO =  mapper.map(chat, ChatVO.class);
                    ChatScreenshots chatScreenshots = null;
                    try {
                        chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    chatVO.setScreenshots(chatScreenshots.getUrls());
                    return chatVO;
                })
                .collectList()
                .map(chats -> APIResponse.builder()
                        .statusCode(200)
                        .message("Chat history fetched successfully")
                        .data(chats)
                        .build());
    }

    @Override
    public Mono<APIResponse> getTransactionChatHistory(Long userId, Long chatId, Long transactionId, int page, int size, String sort, String direction) {
        return null;
    }

    @Override
    public Mono<APIResponse> getChatHistoryBetween(Long userId, Long transactionId, int page, int size, String sort, String direction) {
        Sort sortedBy = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sort);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sortedBy);
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> chatRepository.findBySenderIdAndReceiverIdAndTransactionId(user.getId(), userId, transactionId, pageRequest)
                        .switchIfEmpty(Mono.error(
                                APIException.builder()
                                        .statusCode(404)
                                        .message("No chat history found")
                                        .build()
                        ))
                        .onErrorResume(e -> Mono.error(
                                APIException.builder()
                                        .statusCode(ErrorUtils.getStatusCode(e))
                                        .message(ErrorUtils.getErrorMessage(e))
                                        .build()
                        ))
                        .map(chat -> {
                            ChatVO chatVO =  mapper.map(chat, ChatVO.class);
                            ChatScreenshots chatScreenshots;
                            try {
                                chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            chatVO.setScreenshots(chatScreenshots.getUrls());
                            return chatVO;
                        })
                        .collectList()
                        .map(chats -> APIResponse.builder()
                                .statusCode(200)
                                .message("Chat history fetched successfully")
                                .data(chats)
                                .build()));
    }

    @Override
    @ReactiveRedisCacheable(cacheName = "getMyChatHistory", key = "#userId.toString + '_' + #transactionId.toString() + '_' + #page + '_' + #size + '_' + #sort + '_' + #direction")
    public Flux<ChatVO> getMyChatHistory(long userId, long transactionId, int page, int size, String sort, String direction) {
        Sort sortedBy = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sort);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sortedBy);
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMapMany(user -> chatRepository.findBySenderIdAndReceiverIdAndTransactionId(user.getId(),userId, transactionId, pageRequest)
                        .switchIfEmpty(Mono.error(
                                APIException.builder()
                                        .statusCode(404)
                                        .message("No chat history found")
                                        .build()
                        ))
                        .onErrorResume(e -> Mono.error(
                                APIException.builder()
                                        .statusCode(ErrorUtils.getStatusCode(e))
                                        .message(ErrorUtils.getErrorMessage(e))
                                        .build()
                        ))
                        .map(chat -> {
                            ChatVO chatVO =  mapper.map(chat, ChatVO.class);
                            ChatScreenshots chatScreenshots = null;
                            try {
                                chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            chatVO.setScreenshots(chatScreenshots.getUrls());
                            return chatVO;
                        })
                );
    }

    @Override
    public Mono<APIResponse> getUnreadMessages(long transactionId, int page, int size, String sort, String direction) {
        Sort sortedBy = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sort);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sortedBy);
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> chatRepository.findAllBySenderIdAndTransactionId(user.getId(), transactionId, false, pageRequest)
                        .switchIfEmpty(Mono.error(
                                APIException.builder()
                                        .statusCode(404)
                                        .message("No unread messages found")
                                        .build()
                        ))
                        .onErrorResume(e -> Mono.error(
                                APIException.builder()
                                        .statusCode(ErrorUtils.getStatusCode(e))
                                        .message(ErrorUtils.getErrorMessage(e))
                                        .build()
                        ))
                        .map(chat -> {
                            ChatVO chatVO =  mapper.map(chat, ChatVO.class);
                            ChatScreenshots chatScreenshots = null;
                            try {
                                chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            chatVO.setScreenshots(chatScreenshots.getUrls());
                            return chatVO;
                        })
                        .collectList()
                        .map(chats -> APIResponse.builder()
                                .statusCode(200)
                                .message("Unread messages fetched successfully")
                                .data(chats)
                                .build()));
    }

    @Override
    public Mono<APIResponse> getUnreadMessagesBetween(long userId, long transactionId, int page, int size, String sort, String direction) {
        Sort sortedBy = Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sort);
        PageRequest pageRequest = PageRequest.of(page - 1, size, sortedBy);
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(userRepository::findUsersByEmail)
                .flatMap(user -> chatRepository.findBySenderIdAndReceiverIdAndTransactionId(user.getId(), userId, transactionId, pageRequest)
                        .switchIfEmpty(Mono.error(
                                APIException.builder()
                                        .statusCode(404)
                                        .message("No unread messages found")
                                        .build()
                        ))
                        .onErrorResume(e -> Mono.error(
                                APIException.builder()
                                        .statusCode(ErrorUtils.getStatusCode(e))
                                        .message(ErrorUtils.getErrorMessage(e))
                                        .build()
                        ))
                        .map(chat -> {
                            ChatVO chatVO =  mapper.map(chat, ChatVO.class);
                            ChatScreenshots chatScreenshots = null;
                            try {
                                chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            chatVO.setScreenshots(chatScreenshots.getUrls());
                            return chatVO;
                        })
                        .collectList()
                        .map(chats -> APIResponse.builder()
                                .statusCode(200)
                                .message("Unread messages fetched successfully")
                                .data(chats)
                                .build()));
    }

    @Override
    public Mono<APIResponse> getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(
                        APIException.builder()
                                .statusCode(404)
                                .message("Chat not found")
                                .build()
                ))
                .onErrorResume(e -> Mono.error(
                        APIException.builder()
                                .statusCode(ErrorUtils.getStatusCode(e))
                                .message(ErrorUtils.getErrorMessage(e))
                                .build()
                ))
                .map(chat -> {
                    ChatVO chatVO =  mapper.map(chat, ChatVO.class);
                    ChatScreenshots chatScreenshots = null;
                    try {
                        chatScreenshots = objectMapper.readValue(chat.getScreenshots(), ChatScreenshots.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    chatVO.setScreenshots(chatScreenshots.getUrls());
                    return chatVO;
                })
                .map(chat -> APIResponse.builder()
                        .statusCode(200)
                        .message("Chat fetched successfully")
                        .data(chat)
                        .build());
    }
}
