package org.saultech.suretradeuserservice.utils;

import lombok.NoArgsConstructor;
import org.saultech.suretradeuserservice.exception.APIException;

import java.util.Random;

public class UtilService {

    private UtilService() {
    }

    public static String generate6DigitOTP(Integer length) {
        if (length <= 0) {
            throw APIException.builder()
                    .message("Length must be greater than 0")
                    .statusCode(400)
                    .build();
        }

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomDigit = random.nextInt(10); // Generate a random digit (0 to 9)
            stringBuilder.append(randomDigit);
        }

        return stringBuilder.toString();
    }
}
