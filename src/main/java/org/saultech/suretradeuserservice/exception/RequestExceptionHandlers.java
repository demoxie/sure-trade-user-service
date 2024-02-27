package org.saultech.suretradeuserservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RequestExceptionHandlers {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIError> handleAccessDeniedException(AccessDeniedException ex, ServerWebExchange exchange) {
        APIError apiError = getAPIError("Access Denied", ex.getMessage(), 403, exchange);
        return ResponseEntity.status(403).body(apiError);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIError> handleAPIException(APIException ex, ServerWebExchange exchange) {
        APIError apiError = getAPIError(ex.getMessage(), ex.getMessage(), ex.getStatusCode(), exchange);
        return ResponseEntity.status(ex.getStatusCode()).body(apiError);
    }

    private APIError getAPIError(String message, String errors, int statusCode, ServerWebExchange exchange) {
        return APIError.builder()
                .message(message)
                .error(errors)
                .statusCode(statusCode)
                .path(exchange.getRequest().getPath().value())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
