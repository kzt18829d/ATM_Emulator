package org.kzt18829d.core.type;

import org.kzt18829d.exception.CurrencyTypeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public enum CurrencyType {
    RUB,
    EUR,
    USD;

    private static Map<CurrencyType, String> currencyCode = new ConcurrentHashMap<>();

    public static void setCurrencyCode(Map<CurrencyType, String> currencyCode_) {
        if (currencyCode == null || currencyCode.isEmpty())
            throw new CurrencyTypeException("Currency code map is null or empty");
        currencyCode = new ConcurrentHashMap<>(currencyCode_);
    }

    public static String getCurrencyCode(CurrencyType currencyType) {
        Objects.requireNonNull(currencyType, "Currency type cannot be null");
        return currencyCode.get(currencyType);
    }

    public static Map<CurrencyType, String> getCurrencyCodeMap() {
        return new HashMap<>(currencyCode);
    }
}
