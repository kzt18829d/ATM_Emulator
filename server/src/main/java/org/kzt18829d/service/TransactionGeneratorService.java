package org.kzt18829d.service;

import org.kzt18829d.core.currency.CurrencyType;
import org.kzt18829d.core.identificators.TransactionID;
import org.kzt18829d.exception.IdentificatorException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.kzt18829d.statics.Symbols.*;

public class TransactionGeneratorService {
    private static SecureRandom random = new SecureRandom();

    private static String getRandom(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) stringBuilder.append(SYMBOLS_LET[random.nextInt(SYMBOLS_LET.length)]);
        return stringBuilder.toString();
    }

    private static <T> void checkParameter(T parameter) {
        Objects.requireNonNull(parameter, "One of 'createID' parameters is null");
        if (parameter instanceof String s)
            if (s.isBlank()) throw new IdentificatorException("One of 'createID' parameter is empty");
    }

    public static TransactionID createID(String sender, String receiver, BigDecimal amount, LocalDateTime timestamp, CurrencyType currency) {
        checkParameter(sender);
        checkParameter(receiver);
        checkParameter(amount);
        checkParameter(timestamp);
        checkParameter(currency);

        // SB на случай последующих изменений
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.
                append(CurrencyType.getCurrencyCode(currency))
                .append(sender)
                .append(getRandom(3))
                .append(amount)
                .append(getRandom(1))
                .append(timestamp.getYear())
                .append(getRandom(3))
                .append(receiver)
                .append(getRandom(4))
                .append(timestamp.getMonthValue())
                .append(timestamp.getHour())
                .append(getRandom(2))
                .append(timestamp.getMinute())
                .append(timestamp.getDayOfMonth())
                .append(getRandom(1))
                .append(timestamp.getNano())
                .append(getRandom(3));
        return new TransactionID(stringBuilder.toString());
    }
}
