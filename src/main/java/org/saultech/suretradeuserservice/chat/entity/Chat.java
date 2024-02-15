package org.saultech.suretradeuserservice.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.user.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table("Chats")
public class Chat extends BaseEntity {
    @Column("senderId")
    private Long senderId;
    @Column("receiverId")
    private Long receiverId;
    @Column("transactionId")
    private Long transactionId;
    @Column("giftCardId")
    private Long giftCardId;
    @Column("cryptoCoinId")
    private Long cryptoCoinId;
    @Column("assetName")
    private String assetName;
    @Column("message")
    private String message;
    @Column("screenshots")
    private String screenshots;
    @Column("isRead")
    private Boolean isRead;
}
