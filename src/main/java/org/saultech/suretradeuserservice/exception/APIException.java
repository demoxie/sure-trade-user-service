package org.saultech.suretradeuserservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class APIException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String message;
    private final Integer statusCode;
}
