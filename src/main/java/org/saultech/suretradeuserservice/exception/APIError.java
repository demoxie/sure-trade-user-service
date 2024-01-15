package org.saultech.suretradeuserservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APIError {
    private String message;
    private int statusCode;
    private String error;
    private String path;
    private String timestamp;
}
