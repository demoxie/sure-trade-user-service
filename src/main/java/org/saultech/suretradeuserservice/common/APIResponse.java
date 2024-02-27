package org.saultech.suretradeuserservice.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse implements Serializable {
    @JsonProperty("message")
    private String message;
    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("data")
    private Object data;
}