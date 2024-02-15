package org.saultech.suretradeuserservice.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.common.BaseVO;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatVO extends BaseVO {
    private Long senderId;
    private Long receiverId;
    private Long transactionId;
    private Long giftCardId;
    private Long cryptoCoinId;
    private String assetName;
    private String message;
    private String screenshots;
    private Boolean isRead;
}
