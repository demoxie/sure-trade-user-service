package org.saultech.suretradeuserservice.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

public class ErrorUtils {
    private ErrorUtils() {
    }
    public static Integer getStatusCode(Throwable exception) {
        if (exception instanceof IllegalArgumentException) {
            return 400;
        } else if (exception instanceof NullPointerException) {
            return 404;
        } else if (exception instanceof IllegalStateException) {
            return 409;
        } else if (exception instanceof DuplicateKeyException) {
            return 409;
        }else if (exception instanceof DataAccessException){
            return 400;
        }else if (exception instanceof io.r2dbc.spi.R2dbcDataIntegrityViolationException){
            return 409;
        }else if (exception instanceof io.r2dbc.spi.R2dbcBadGrammarException){
            return 400;
        }else if (exception instanceof io.r2dbc.spi.R2dbcNonTransientResourceException){
            return 500;
        }
        return 500;
    }

    public static String getErrorMessage(Throwable exception){
        if(exception.getCause() != null){
            return exception.getCause().getMessage();
        }
        return exception.getMessage();
    }
}
