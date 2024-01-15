package org.saultech.suretradeuserservice.messaging.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushyMessage {
    @JsonProperty("to")
    private String to;
    @JsonProperty("data")
    private NotificationData data;
    @JsonProperty("notification")
    private DeviceNotificationProperties notification;
}
