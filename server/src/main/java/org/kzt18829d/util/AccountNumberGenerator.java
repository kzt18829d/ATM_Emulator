package org.kzt18829d.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

import static org.kzt18829d.statics.Symbols.SYMBOLS_ALL;

public class AccountNumberGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AccountNumberGenerator.class);
    private static final SecureRandom random = new SecureRandom();

    // VALUE in settings.env
    // TODO: Add to Environment Configuration
    /// Param name "ACCOUNT_NUMBER_VALIDATOR_NUM_LENGTH"
    private static volatile int NUM_LENGTH = 20;

    public static void setNumLength(int length) {
        if (length < 5) {
            logger.warn("Incorrect length value: {}. Will be used default value: {}", length, NUM_LENGTH);
            return;
        }
        NUM_LENGTH = length;
        logger.info("Set new length: {}", length);
    }

    public static String generate() {
        StringBuilder string = new StringBuilder(NUM_LENGTH);
        for (int i = 0; i < NUM_LENGTH; i++)
            string.append(SYMBOLS_ALL[random.nextInt(SYMBOLS_ALL.length)]);
        return string.toString();
    }
}
