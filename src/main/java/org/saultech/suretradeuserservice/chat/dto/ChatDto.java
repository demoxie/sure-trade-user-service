package org.saultech.suretradeuserservice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.common.BaseVO;
import org.saultech.suretradeuserservice.products.giftcard.dto.Screenshots;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatDto {
    private Long senderId;
    private Long receiverId;
    private Long transactionId;
    private Long giftCardId;
    private Long cryptoCoinId;
    private String assetName;
    private String message;
    private ChatScreenshots screenshots;
    private boolean isRead;
}
