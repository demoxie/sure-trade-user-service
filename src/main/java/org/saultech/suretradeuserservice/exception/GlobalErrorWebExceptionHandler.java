package org.saultech.suretradeuserservice.exception;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import java.util.Map;

@Component
@Order(-2)
@Slf4j
public class GlobalErrorWebExceptionHandler extends
        AbstractErrorWebExceptionHandler {
    private final ModelMapper mapper;
    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer, ServerCodecConfigurer serverCodecConfigurer1, ModelMapper mapper) {
        super(errorAttributes, resources, applicationContext);
        this.mapper = mapper;
        super.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable throwable = getError(request);
        Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        log.error("Error attributes: {}", errorAttributes);
        APIError apiError = mapper.map(errorAttributes, APIError.class);
        apiError.setStatusCode((int) errorAttributes.get("status"));
        int status = (int) errorAttributes.get("status");

        if (throwable instanceof APIException apiException) {
            log.error("APIException is thrown: {}", apiException.getMessage());
            return handleApiException(apiException,apiError);
        } else if (throwable instanceof ResponseStatusException responseStatusException) {
            log.error("ResponseStatusException is thrown: {}", responseStatusException.getMessage());
            return handleResponseStatusException(responseStatusException,apiError);
        }else if (throwable instanceof AuthenticationException authenticationException) {
            log.error("AuthenticationException is thrown: {}", authenticationException.getMessage());
            return handleAuthenticationException(authenticationException,apiError);
        }else if(throwable instanceof AccessDeniedException accessDeniedException) {
            log.error("AccessDeniedException is thrown: {}", accessDeniedException.getMessage());
            return handleAccessDeniedException(accessDeniedException,apiError);
        }


        log.error("Error is thrown: {}", throwable.getMessage());

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(apiError));
    }

    private Mono<ServerResponse> handleAccessDeniedException(AccessDeniedException accessDeniedException, APIError apiError) {
        apiError.setMessage(accessDeniedException.getMessage());
        apiError.setError(accessDeniedException.getLocalizedMessage());
        apiError.setStatusCode(HttpStatus.FORBIDDEN.value());
        return ServerResponse.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(apiError));
    }

    private Mono<ServerResponse> handleAuthenticationException(AuthenticationException authenticationException, APIError apiError) {
        apiError.setMessage(authenticationException.getMessage());
        apiError.setError(authenticationException.getLocalizedMessage());
        apiError.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(apiError));
    }

    private Mono<ServerResponse> handleResponseStatusException(ResponseStatusException responseStatusException, APIError apiError) {
        apiError.setMessage(responseStatusException.getMessage());
        apiError.setError(responseStatusException.getLocalizedMessage());
        apiError.setStatusCode(responseStatusException.getStatusCode().value());
        return ServerResponse.status(responseStatusException.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(apiError));
    }

    private Mono<ServerResponse> handleApiException(APIException apiException,APIError apiError) {
        apiError.setMessage(apiException.getMessage());
        apiError.setError(apiException.getLocalizedMessage());
        apiError.setStatusCode(apiException.getStatusCode());
        return ServerResponse.status(apiException.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(apiError));
    }
}