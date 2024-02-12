package org.saultech.suretradeuserservice.chat.repository;

import org.saultech.suretradeuserservice.chat.entity.Chat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRepository extends R2dbcRepository<Chat, Long> {
    Flux<Chat> findBySenderIdAndReceiverIdAndTransactionId(Long senderId, Long receiverId, Long transactionId, PageRequest pageRequest);

    Flux<Chat> findBySenderId(Long userId, PageRequest pageRequest);

    Flux<Chat> findAllBySenderIdAndTransactionId(Long senderId, long transactionId, boolean b, PageRequest pageRequest);
}
