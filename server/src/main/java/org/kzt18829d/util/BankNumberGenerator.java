package org.kzt18829d.util;

import org.kzt18829d.core.type.CurrencyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Map;

import static org.kzt18829d.statics.Symbols.INTEGERS_ARRAY;

public class BankNumberGenerator {
    private static final Logger logger = LoggerFactory.getLogger(BankNumberGenerator.class);
    private static final SecureRandom random = new SecureRandom();
    // In settings.env
    /// Key name "BANK_ACCOUNT_ID_INDEX"
    private static volatile String INDEX = "40006";
    //in settings.env
    /// key name "BANK_NUMBER_GENERATOR_LENGTH"
    private static volatile int LENGTH = 25;

    //in settings.env
    // RUB, EURO, USD
    /// Keys: "BANK_NUMBER_GENERATOR_RUB", "BANK_NUMBER_GENERATOR_EUR", "BANK_NUMBER_GENERATOR_USD"
    private static volatile Map<CurrencyType, String> currencyINDEX = Map.of(
            CurrencyType.RUB, "188403",
            CurrencyType.USD, "716638",
            CurrencyType.EUR, "837740"
    );

    public static void setCurrencyINDEX(Map<CurrencyType, String> map) {
        if (map == null || map.isEmpty()) {
            logger.warn("Null or empty currency index map. Using default");
            return;
        }
        currencyINDEX = map;
    }

    public static void setINDEX(String index) {
        if (index == null || index.isBlank()) {
            logger.warn("Null or empty index value. Using default: {}", INDEX);
            return;
        }
        INDEX = index;
    }

    public static void setLENGTH(int length) {
        if (length < 25) {
            logger.warn("Incorrect number length: {}. Using default: {}", length, LENGTH);
            return;
        }
        LENGTH = length;
    }

    public static int getLength() {
        return LENGTH;
    }

    public static int getIndexLength() {
        return INDEX.length();
    }

    public static String generate(CurrencyType currencyType) {
        if (!currencyINDEX.containsKey(currencyType)) {
            throw new IllegalArgumentException("Invalid currency type");
        }
        var currencyIndex = currencyINDEX.get(currencyType);
        StringBuilder bankAccountIndex = new StringBuilder(LENGTH);
        int currentLength = 0;
        bankAccountIndex.append(INDEX);
        bankAccountIndex.append(currencyIndex);
        currentLength = bankAccountIndex.length();
        for (int i = currentLength; i < LENGTH; i++)
            bankAccountIndex.append(INTEGERS_ARRAY[random.nextInt(INTEGERS_ARRAY.length)]);
        return bankAccountIndex.toString();
    }
}
