package org.saultech.suretradeuserservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTelegramDto {
    private String chatId;
    private String username;
}
