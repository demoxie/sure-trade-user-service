package org.saultech.suretradeuserservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
public class LoggingService {
    private LoggingService(){

    }
    public static void logRequest(Object request, String service, String endpoint, String method) {
        log.info("Making {} request to {} at {} with body: {}", method, service, endpoint, request);
    }

    public static void logResponse(Object response, String service, String endpoint) {
        log.info("Response from {} at {}: {}", service, endpoint, response);
    }

    public static void logError(Object error, String service, String endpoint) {
        log.error("Error from {} at {}: {}", service, endpoint, error);
    }
}
