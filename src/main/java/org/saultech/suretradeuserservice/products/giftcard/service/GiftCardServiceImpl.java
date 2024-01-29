package org.saultech.suretradeuserservice.products.giftcard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.common.APIResponse;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.messaging.email.Email;
import org.saultech.suretradeuserservice.messaging.notification.DeviceNotificationProperties;
import org.saultech.suretradeuserservice.messaging.notification.NotificationData;
import org.saultech.suretradeuserservice.messaging.notification.PushyMessage;
import org.saultech.suretradeuserservice.messaging.telegram.TelegramMessage;
import org.saultech.suretradeuserservice.products.giftcard.dto.*;
import org.saultech.suretradeuserservice.products.giftcard.enums.TransactionStatus;
import org.saultech.suretradeuserservice.products.giftcard.vo.GiftCardTransactionVO;
import org.saultech.suretradeuserservice.rabbitmq.service.Producer;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.enums.Role;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.saultech.suretradeuserservice.user.service.UserService;
import org.saultech.suretradeuserservice.utils.ErrorUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardServiceImpl implements GiftCardService{
    private final UserRepository userRepository;
    private final APIClientService apiClientService;
    private final UserService userService;
    private final Producer producer;
    private final ModelMapper mapper;
    @Override
    public Mono<APIResponse> getGiftCardTransactions(int page, int size, String sort, String direction) {
        return apiClientService.makePostRequestWithoutQueryParamsWithFluxResponse("/gift-card/transaction/get-all", "product", GetMyGiftCardTransactionsWithOthersDto
                .builder()
                .page(page)
                .size(size)
                .sortBy(sort)
                .direction(direction)
                .build(), "GiftCardTransactionVO");
    }


    @Override
    public Mono<APIResponse> createGiftCardTransaction(CreateGiftCardTransactionDto createGiftCardTransactionDto) {
        return userService.getCurrentUser()
                .flatMap(user -> {
                    createGiftCardTransactionDto.setUserId(user.getId());
                    GiftCardDto giftCardDto = mapper.map(createGiftCardTransactionDto, GiftCardDto.class);
                    giftCardDto.setStatus(TransactionStatus.NEW.getStatus());
                    DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    giftCardDto.setExpiryDate(LocalDate.parse(createGiftCardTransactionDto.getExpiryDate().toString(), formatter));
                    giftCardDto.setUserId(user.getId());
                    Screenshots screenshots = Screenshots.builder()
                            .url(createGiftCardTransactionDto.getScreenshots())
                            .build();
                    giftCardDto.setScreenshots(screenshots);
                    createGiftCardTransactionDto.setGiftCardDto(giftCardDto);
                    createGiftCardTransactionDto.setUserId(user.getId());
                    createGiftCardTransactionDto.setStatus(TransactionStatus.NEW.getStatus());
                    return apiClientService.makePostRequestWithoutQueryParamsWithMonoReturned("/gift-card/transaction", "product", createGiftCardTransactionDto, "GiftCardTransactionVO")
                            .flatMap(apiResponse -> {
                                if (apiResponse.getStatusCode() == 200) {
                                    GiftCardTransactionVO vo = (GiftCardTransactionVO) apiResponse.getData();
                                    if(user.getRoles().equals(Role.USER)){
                                        return userRepository.findById(vo.getMerchantId())
                                                .flatMap(merchant -> {
                                                    log.info("Merchant: {}", merchant.getEmail());
                                                    sendMessageAsUser(createGiftCardTransactionDto, user, merchant, vo, giftCardDto);
                                                    return Mono.just(apiResponse);
                                                });
                                    }
                                    return userRepository.findById(vo.getUserId())
                                            .flatMap(user1 -> {
                                                sendMessageAsMerchant(user, user1, vo);

                                                return Mono.just(apiResponse);
                });
                                }
                                return Mono.just(apiResponse);
                            });
                });

    }

    private void sendMessageAsUser(CreateGiftCardTransactionDto createGiftCardTransactionDto, User user, User merchant, GiftCardTransactionVO vo, GiftCardDto giftCardDto) {
        Map<String, Object> merchantMailData = Map.of(
                "transactionReference", vo.getReferenceNo(),
                "name", merchant.getUsername(),
                "cardType", giftCardDto.getCardType(),
                "cardIssuer", giftCardDto.getCardIssuer(),
                "quantity", giftCardDto.getQuantity(),
                "pricePerUnit", giftCardDto.getAmount(),
                "cardCurrency", giftCardDto.getCurrency(),
                "paymentCurrency", createGiftCardTransactionDto.getCurrency()
        );
        Email emailToMerchant = Email.builder()
                .to(merchant.getEmail())
                .subject("New Transaction")
                .body(merchantMailData)
                .template("gift-card-transaction")
                .build();
        PushyMessage notificationToMerchant = PushyMessage.builder()
                .to(merchant.getTelegramChatId())
                .notification(
                        DeviceNotificationProperties.builder()
                                .title("New Transaction")
                                .badge(1)
                                .sound("ping.aiff")
                                .body("You have a new transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .data(
                        NotificationData.builder()
                                .message("You have a new transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .build();
        producer.sendEmail(emailToMerchant);

        TelegramMessage telegramMessageToMerchant = TelegramMessage.builder()
                .chatId(merchant.getTelegramChatId())
                .message("You have a new transaction with reference number "+ vo.getReferenceNo())
                .build();
        producer.sendTelegram(telegramMessageToMerchant);
        producer.sendNotification(notificationToMerchant);
        Map<String, Object> feedBackData = Map.of(
                "transactionReference", vo.getReferenceNo(),
                "username", user.getUsername()
        );
        Email sendFeedBackEmail = Email.builder()
                .to(user.getEmail())
                .subject("New Transaction")
                .body(feedBackData)
                .template("new-transaction-feedback")
                .build();
        producer.sendEmail(sendFeedBackEmail);

        NotificationData notificationData = NotificationData.builder()
                .message("Your transaction with reference number "+ vo.getReferenceNo()+" has been created")
                .build();
        PushyMessage notificationToUser = PushyMessage.builder()
                .to(user.getTelegramChatId())
                .notification(
                        DeviceNotificationProperties.builder()
                                .title("New Transaction")
                                .badge(1)
                                .sound("ping.aiff")
                                .body("Your transaction with reference number "+ vo.getReferenceNo()+" has been created")
                                .build()
                )
                .data(notificationData)
                .build();

        producer.sendNotification(notificationToUser);

        TelegramMessage telegramMessageToUser = TelegramMessage.builder()
                .chatId(user.getTelegramChatId())
                .message("Your transaction with reference number "+ vo.getReferenceNo()+" has been created")
                .build();
        producer.sendTelegram(telegramMessageToUser);
    }

    private void sendMessageAsMerchant(User user, User user1, GiftCardTransactionVO vo) {
        Map<String, Object> data = Map.of(
                "transactionReference", vo.getReferenceNo(),
                "name", user1.getUsername()
        );
        Email emailToMerchant = Email.builder()
                .to(user1.getEmail())
                .subject("New Transaction")
                .body(data)
                .template("new-transaction")
                .build();
        PushyMessage notificationToMerchant = PushyMessage.builder()
                .to(user1.getTelegramChatId())
                .notification(
                        DeviceNotificationProperties.builder()
                                .title("New Transaction")
                                .badge(1)
                                .sound("ping.aiff")
                                .body("You have a new transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .data(
                        NotificationData.builder()
                                .message("You have a new transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .build();
        producer.sendEmail(emailToMerchant);
        producer.sendNotification(notificationToMerchant);

        Map<String, Object> feedBackData = Map.of(
                "transactionReference", vo.getReferenceNo(),
                "username", user.getUsername()
        );

        Email sendFeedBackEmail = Email.builder()
                .to(user.getEmail())
                .subject("New Transaction")
                .body(feedBackData)
                .template("new-transaction-feedback")
                .build();

        producer.sendEmail(sendFeedBackEmail);

        NotificationData notificationData = NotificationData.builder()
                .message("Your transaction with reference number "+ vo.getReferenceNo()+" has been created")
                .build();

        PushyMessage notificationToUser = PushyMessage.builder()
                .to(user.getTelegramChatId())
                .notification(
                        DeviceNotificationProperties.builder()
                                .title("New Transaction")
                                .badge(1)
                                .sound("ping.aiff")
                                .body("Your transaction with reference number "+ vo.getReferenceNo()+" has been created")
                                .build()
                )
                .data(notificationData)
                .build();

        producer.sendNotification(notificationToUser);

        TelegramMessage telegramMessageToUser = TelegramMessage.builder()
                .chatId(user.getTelegramChatId())
                .message("Your transaction with reference number "+ vo.getReferenceNo()+" has been created")
                .build();

        producer.sendTelegram(telegramMessageToUser);
    }

    @Override
    public Mono<APIResponse> getGiftCardTransaction(Long id) {
        return apiClientService.makeGetRequestWithoutQueryParamsWithMonoReturned("/gift-card/transaction/"+id, "product", "GiftCardTransactionVO");
    }

    @Override
    public Mono<APIResponse> getGiftCardTransactionByReferenceNo(String referenceNo) {
        return apiClientService.makeGetRequestWithoutQueryParamsWithMonoReturned("/gift-card/transaction/references/"+referenceNo, "product", "GiftCardTransactionVO");
    }

    @Override
    public Mono<APIResponse> getGiftCardTransactionsWithOthers(int page, int size, String sort, String direction) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String username = securityContext.getAuthentication().getName();
                    log.info("Username: {}", username);
                    return userService.getUserByUsername(username);
                })
                .flatMap(user -> {
                        GetMyGiftCardTransactionsWithOthersDto dto = GetMyGiftCardTransactionsWithOthersDto
                                .builder()
                                .userId(user.getId())
                                .role(user.getRoles().name())
                                .page(page)
                                .size(size)
                                .sortBy(sort)
                                .direction(direction)
                                .build();
                        return apiClientService.makePostRequestWithoutQueryParamsWithFluxResponse("/gift-card/transaction/mine/with/others", "product",dto, "GiftCardTransactionVO");
                });
    }

    @Override
    public Mono<APIResponse> getMyGiftCardTransactions(int page, int size, String sort, String direction) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String username = securityContext.getAuthentication().getName();
                    log.info("Username: {}", username);
                    return userService.getUserByUsername(username);
                })
                .flatMap(user -> {
                    GetMyGiftCardTransactionsWithOthersDto dto = GetMyGiftCardTransactionsWithOthersDto
                            .builder()
                            .userId(user.getId())
                            .role(user.getRoles().name())
                            .page(page)
                            .size(size)
                            .sortBy(sort)
                            .direction(direction)
                            .build();
                    return apiClientService.makePostRequestWithoutQueryParamsWithFluxResponse("/gift-card/transaction/mine", "product",dto, "GiftCardTransactionVO");
                });
    }

    @Override
    public Mono<APIResponse> addGiftCard(GiftCardDto dto) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String username = securityContext.getAuthentication().getName();
                    log.info("Username: {}", username);
                    return userService.getUserByUsername(username);
                })
                .flatMap(user -> {
                    dto.setUserId(user.getId());
                    return apiClientService.makePostRequestWithoutQueryParamsWithMonoReturned("/gift-cards/", "product",dto, "GiftCardVO");
                });
    }

    @Override
    public Mono<APIResponse> getGiftCards(
            int page,
            int size,
            String sort,
            String direction
    ) {
        Map<String, Object> queryParams = Map.of(
                "page", page,
                "size", size,
                "sort", sort,
                "direction", direction
        );
        return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/gift-cards/", "product", queryParams,"GiftCardVO");
    }

    @Override
    public Mono<APIResponse> getGiftCardTransactionsByStatus(String status, int page, int size, String sort, String direction) {
        Map<String, Object> queryParams = Map.of(
                "page", page,
                "size", size,
                "sort", sort,
                "direction", direction,
                "status", status
        );
        return apiClientService.makeGetRequestWithQueryParamsAndFluxReturned("/gift-card/transaction/filter/"+status, "product", queryParams,"GiftCardTransactionVO");
    }

    @Override
    public Mono<APIResponse> cancelMyGiftCardTransaction(Long transactionId, CancelRequestDto dto) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String username = securityContext.getAuthentication().getName();
                    log.info("Username: {}", username);
                    return userService.getUserByUsername(username);
                })
                .flatMap(user -> {
                    dto.setUserId(user.getId());

                    return apiClientService.makePutRequestWithoutQueryParamsWithMonoReturned("/gift-card/transaction/my-transactions/"+transactionId+"/cancel", "product",dto, "GiftCardTransactionVO")
                            .flatMap(apiResponse -> {
                                if (apiResponse.getStatusCode() == 200) {
                                    GiftCardTransactionVO vo = (GiftCardTransactionVO) apiResponse.getData();
                                    if(user.getRoles().equals(Role.USER)){
                                        return userRepository.findById(vo.getMerchantId())
                                                .flatMap(merchant -> {
                                                    log.info("Merchant: {}", merchant.getEmail());
                                                    Map<String, Object> data = Map.of(
                                                            "transactionReference", vo.getReferenceNo(),
                                                            "reason", dto.getCancelReason(),
                                                            "name", merchant.getUsername()
                                                    );
                                                    Email emailToMerchant = Email.builder()
                                                            .to(merchant.getEmail())
                                                            .subject("Transaction Cancelled")
                                                            .body(data)
                                                            .template("transaction-cancelled")
                                                            .build();
                                                    Email feedBackEmail = Email.builder()
                                                            .to(user.getEmail())
                                                            .subject("Transaction Cancelled")
                                                            .body(data)
                                                            .template("transaction-cancelled-feedback")
                                                            .build();
                                                    PushyMessage notificationToMerchant = PushyMessage.builder()
                                                            .to(merchant.getTelegramChatId()    )
                                                            .notification(
                                                                    DeviceNotificationProperties.builder()
                                                                            .title("Transaction Cancelled")
                                                                            .badge(1)
                                                                            .sound("ping.aiff")
                                                                            .body("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                            .build()
                                                            )
                                                            .data(
                                                                    NotificationData.builder()
                                                                            .message("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                            .build()
                                                            )
                                                            .build();
                                                    PushyMessage notificationToUser = PushyMessage.builder()
                                                            .to(user.getTelegramChatId())
                                                            .notification(
                                                                    DeviceNotificationProperties.builder()
                                                                            .title("Transaction Cancelled")
                                                                            .badge(1)
                                                                            .sound("ping.aiff")
                                                                            .body("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                            .build()
                                                            )
                                                            .data(
                                                                    NotificationData.builder()
                                                                            .message("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                            .build()
                                                            )
                                                            .build();
                                                    producer.sendEmail(emailToMerchant);
                                                    producer.sendEmail(feedBackEmail);
                                                    producer.sendNotification(notificationToMerchant);
                                                    producer.sendNotification(notificationToUser);
                                                    return Mono.just(apiResponse);
                                                });
                                    }
                                   return userRepository.findById(vo.getUserId())
                                            .flatMap(user1 -> {
                                                Map<String, Object> data = Map.of(
                                                        "transactionReference", vo.getReferenceNo(),
                                                        "reason", dto.getCancelReason(),
                                                        "name", user1.getUsername()
                                                );
                                                Email emailToMerchant = Email.builder()
                                                        .to(user1.getEmail())
                                                        .subject("Transaction Cancelled")
                                                        .body(data)
                                                        .template("transaction-cancelled")
                                                        .build();
                                                Email feedBackEmail = Email.builder()
                                                        .to(user.getEmail())
                                                        .subject("Transaction Cancelled")
                                                        .body(data)
                                                        .template("transaction-cancelled-feedback")
                                                        .build();
                                                PushyMessage notificationToMerchant = PushyMessage.builder()
                                                        .to(user1.getTelegramChatId())
                                                        .notification(
                                                                DeviceNotificationProperties.builder()
                                                                        .title("Transaction Cancelled")
                                                                        .badge(1)
                                                                        .sound("ping.aiff")
                                                                        .body("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                        .build()
                                                        )
                                                        .data(
                                                                NotificationData.builder()
                                                                        .message("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                        .build()
                                                        )
                                                        .build();
                                                PushyMessage notificationToUser = PushyMessage.builder()
                                                        .to(user.getTelegramChatId())
                                                        .notification(
                                                                DeviceNotificationProperties.builder()
                                                                        .title("Transaction Cancelled")
                                                                        .badge(1)
                                                                        .sound("ping.aiff")
                                                                        .body("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                        .build()
                                                        )
                                                        .data(
                                                                NotificationData.builder()
                                                                        .message("Your transaction with reference number "+vo.getReferenceNo()+" has been cancelled")
                                                                        .build()
                                                        )
                                                        .build();
                                                producer.sendEmail(emailToMerchant);
                                                producer.sendEmail(feedBackEmail);
                                                producer.sendNotification(notificationToMerchant);
                                                producer.sendNotification(notificationToUser);
                                                return Mono.just(apiResponse);
                                            });
                                }
                                return Mono.just(apiResponse);
                            });
                });
    }

    @Override
    public Mono<APIResponse> acceptOrCancelTransaction(long transactionId, AcceptRejectTransactionDto dto) {
        return apiClientService.makePutRequestWithoutQueryParamsWithMonoReturned("/gift-card/transaction/"+transactionId+"/accept-or-reject", "product",dto, "GiftCardTransactionVO")
                .flatMap(apiResponse -> {
                    if (apiResponse.getStatusCode() == 200) {
                        GiftCardTransactionVO vo = (GiftCardTransactionVO) apiResponse.getData();
                        return userRepository.findById(vo.getUserId())
                                .flatMap(user -> {
                                    if (dto.getStatus().equalsIgnoreCase("ACCEPTED")){
                                        return sendTransactionAcceptedMessage(apiResponse, user, vo);
                                    }
                                    return sendTransactionDeclinedMessage(apiResponse, user, vo);
                                });
                    }
                    return Mono.just(apiResponse);
                });
    }


    @NotNull
    private Mono<APIResponse> sendTransactionAcceptedMessage(APIResponse apiResponse, User user, GiftCardTransactionVO vo) {
        Map<String, Object> feedBackMailData = Map.of(
                "transactionReference", vo.getReferenceNo(),
                "name", user.getUsername()
        );
        Email feedBackEmail = Email.builder()
                .to(user.getEmail())
                .subject("Transaction Cancelled")
                .body(feedBackMailData)
                .template("transaction-accepted-feedback")
                .build();
        PushyMessage notificationFeedBack = PushyMessage.builder()
                .to(user.getTelegramChatId())
                .notification(
                        DeviceNotificationProperties.builder()
                                .title("Transaction Accepted")
                                .badge(1)
                                .sound("ping.aiff")
                                .body("You've accepted the transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .data(
                        NotificationData.builder()
                                .message("You've accepted the transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .build();

        TelegramMessage telegramMessageFeedBack = TelegramMessage.builder()
                .chatId(user.getTelegramChatId())
                .message("You've accepted the transaction with reference number "+ vo.getReferenceNo())
                .build();
        producer.sendEmail(feedBackEmail);
        producer.sendNotification(notificationFeedBack);
        producer.sendTelegram(telegramMessageFeedBack);
        return userRepository.findById(vo.getMerchantId())
                .switchIfEmpty(
                        Mono.error(
                                APIException.builder()
                                        .statusCode(404)
                                        .message("User not found")
                                        .build()
                        )
                )
                .onErrorResume(
                        ex -> Mono.error(
                                APIException.builder()
                                        .statusCode(ErrorUtils.getStatusCode(ex))
                                        .message(ErrorUtils.getErrorMessage(ex))
                                        .build()
                        )
                )
                .flatMap(cardOwner -> {
                    Map<String, Object> data = Map.of(
                            "transactionReference", vo.getReferenceNo(),
                            "name", cardOwner.getUsername()
                    );
                    Email emailToCardOwner = Email.builder()
                            .to(cardOwner.getEmail())
                            .subject("Transaction Accepted")
                            .body(data)
                            .template("transaction-accepted")
                            .build();
                    PushyMessage notificationToOwner = PushyMessage.builder()
                            .to(cardOwner.getTelegramChatId())
                            .notification(
                                    DeviceNotificationProperties.builder()
                                            .title("Transaction Accepted")
                                            .badge(1)
                                            .sound("ping.aiff")
                                            .body("Your transaction with reference number " + vo.getReferenceNo() + " has been accepted")
                                            .build()
                            )
                            .data(
                                    NotificationData.builder()
                                            .message("Your transaction with reference number " + vo.getReferenceNo() + " has been accepted")
                                            .build()
                            )
                            .build();

                    TelegramMessage telegramMessageToOwner = TelegramMessage.builder()
                            .chatId(cardOwner.getTelegramChatId())
                            .message("Your transaction with reference number " + vo.getReferenceNo() + " has been accepted")
                            .build();
                    producer.sendEmail(emailToCardOwner);
                    producer.sendNotification(notificationToOwner);
                    producer.sendTelegram(telegramMessageToOwner);
                    return Mono.just(apiResponse);
                });
    }

    private Mono<APIResponse> sendTransactionDeclinedMessage(APIResponse apiResponse, User user, GiftCardTransactionVO vo) {
        Map<String, Object> feedBackMailData = Map.of(
                "transactionReference", vo.getReferenceNo(),
                "name", user.getUsername()
        );
        Email feedBackEmail = Email.builder()
                .to(user.getEmail())
                .subject("Transaction Declined")
                .body(feedBackMailData)
                .template("transaction-declined-feedback")
                .build();
        PushyMessage notificationFeedBack = PushyMessage.builder()
                .to(user.getTelegramChatId())
                .notification(
                        DeviceNotificationProperties.builder()
                                .title("Transaction Declined")
                                .badge(1)
                                .sound("ping.aiff")
                                .body("You've declined the transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .data(
                        NotificationData.builder()
                                .message("You've declined the transaction with reference number "+ vo.getReferenceNo())
                                .build()
                )
                .build();

        TelegramMessage telegramMessageFeedBack = TelegramMessage.builder()
                .chatId(user.getTelegramChatId())
                .message("You've declined the transaction with reference number "+ vo.getReferenceNo())
                .build();
        producer.sendEmail(feedBackEmail);
        producer.sendNotification(notificationFeedBack);
        producer.sendTelegram(telegramMessageFeedBack);

        return userRepository.findById(vo.getMerchantId())
                .switchIfEmpty(
                        Mono.error(
                                APIException.builder()
                                        .statusCode(404)
                                        .message("User not found")
                                        .build()
                        )
                )
                .onErrorResume(
                        ex -> Mono.error(
                                APIException.builder()
                                        .statusCode(ErrorUtils.getStatusCode(ex))
                                        .message(ErrorUtils.getErrorMessage(ex))
                                        .build()
                        )
                )
                .flatMap(cardOwner -> {
                    Map<String, Object> data = Map.of(
                            "transactionReference", vo.getReferenceNo(),
                            "name", cardOwner.getUsername()
                    );
                    Email emailToCardOwner = Email.builder()
                            .to(cardOwner.getEmail())
                            .subject("Transaction Declined")
                            .body(data)
                            .template("transaction-declined-feedback")
                            .build();
                    PushyMessage notificationToOwner = PushyMessage.builder()
                            .to(cardOwner.getTelegramChatId())
                            .notification(
                                    DeviceNotificationProperties.builder()
                                            .title("Transaction Declined")
                                            .badge(1)
                                            .sound("ping.aiff")
                                            .body("Your transaction with reference number " + vo.getReferenceNo() + " has been declined")
                                            .build()
                            )
                            .data(
                                    NotificationData.builder()
                                            .message("Your transaction with reference number " + vo.getReferenceNo() + " has been declined")
                                            .build()
                            )
                            .build();

                    TelegramMessage telegramMessageToOwner = TelegramMessage.builder()
                            .chatId(cardOwner.getTelegramChatId())
                            .message("Your transaction with reference number " + vo.getReferenceNo() + " has been declined")
                            .build();
                    producer.sendEmail(emailToCardOwner);
                    producer.sendNotification(notificationToOwner);
                    producer.sendTelegram(telegramMessageToOwner);
                    return Mono.just(apiResponse);
                });
    }
}
