package org.saultech.suretradeuserservice.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.saultech.suretradeuserservice.chat.dto.ChatDto;
import org.saultech.suretradeuserservice.chat.service.ChatService;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Validated
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/")
    public Mono<APIResponse> sendMessage(@Valid @RequestBody ChatDto chatDto) {
        LoggingService.logRequest(chatDto, "Chat Service", "/chats/", "POST");
        return chatService.sendMessage(chatDto);
    }

    @GetMapping("/{chatId}")
    public Mono<APIResponse> getChatById(@PathVariable Long chatId) {
        return chatService.getChatById(chatId);
    }

    @DeleteMapping("/delete")
    public Mono<APIResponse> deleteMessage(@RequestParam Long chatId) {
        return chatService.deleteMessage(chatId);
    }

    @PostMapping("/mark-as-read")
    public Mono<APIResponse> markAsRead(@RequestParam Long chatId) {
        return chatService.markAsRead(chatId);
    }

    @GetMapping("/user/{userId}/transactions/{transactionId}")
    public Mono<APIResponse> getChatHistoryBetween(@PathVariable Long userId, @PathVariable Long transactionId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "message") String sort, @RequestParam(defaultValue = "DESC") String direction) {
        return chatService.getChatHistoryBetween(userId, transactionId, page, size, sort, direction);
    }

    @GetMapping("/user-chat-history")
    public Mono<APIResponse> getUserChatHistory(@RequestParam Long userId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "message") String sort, @RequestParam(defaultValue = "DESC") String direction) {
        return chatService.getUserChatHistory(userId, page, size, sort, direction);
    }

    @GetMapping("/transaction-chat-history")
    public Mono<APIResponse> getTransactionChatHistory(@RequestParam Long userId, @RequestParam Long chatId, @RequestParam Long transactionId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "message") String sort, @RequestParam(defaultValue = "DESC") String direction) {
        return chatService.getTransactionChatHistory(userId, chatId, transactionId, page, size, sort, direction);
    }

    @GetMapping("/between/{userId}/me/transactions/{transactionId}")
    public Mono<APIResponse> getMyChatHistory(
            @PathVariable long userId,
            @PathVariable long transactionId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "message") String sort, @RequestParam(defaultValue = "DESC") String direction
    ) {
        return chatService.getMyChatHistory(userId, transactionId, page, size, sort, direction);
    }

    @GetMapping("/unread/get-all/transactions/{transactionId}")
    public Mono<APIResponse> getUnreadMessages(@PathVariable long transactionId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "message") String sort, @RequestParam(defaultValue = "DESC") String direction){
        return chatService.getUnreadMessages(transactionId, page, size, sort, direction);
    }

    @GetMapping("my/unread/users/{userId}/get-all/transactions/{transactionId}")
    public Mono<APIResponse> getUnreadMessagesBetween(@PathVariable long userId, @PathVariable long transactionId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "message") String sort, @RequestParam(defaultValue = "DESC") String direction){
        return chatService.getUnreadMessagesBetween(userId, transactionId, page, size, sort, direction);
    }

}
