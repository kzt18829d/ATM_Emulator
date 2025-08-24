package org.kzt18829d.service;

import org.kzt18829d.core.type.CurrencyType;
import org.kzt18829d.exception.EnvironmentException;
import org.kzt18829d.util.AccountNumberGenerator;
import org.kzt18829d.util.BankNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnvironmentConfiguratorService {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfiguratorService.class);
    private static final Object lock = new Object();

    private static Map<String, String> configuration;

    private static final class InstanceHolder {
        private static final EnvironmentConfiguratorService Instance = new EnvironmentConfiguratorService();
    }

    private EnvironmentConfiguratorService() {}

    public static EnvironmentConfiguratorService getInstance() {
        return InstanceHolder.Instance;
    }

    public void setConfiguration(Map<String, String> newConfiguration) {
        configuration = new ConcurrentHashMap<>(newConfiguration);
    }

    public void executeConfiguration() {
        logger.info("Executing configuration of environments...");
        if (Objects.isNull(configuration)) {
            logger.error("Configuration could not be applied: NullPointerException");
            throw new NullPointerException("Configuration could not be applied");
        }
        if (configuration.isEmpty()) {
            logger.error("Configuration could not be applied: Empty configuration map");
            throw new EnvironmentException("Configuration could not be applied: Empty configuration map");
        }


    }

    public void configAccountNumberGenerator() {
        logger.info("Installing AccountNumberGenerator configuration");

        int length = getInt("ACCOUNT_NUMBER_GENERATOR_NUM_LENGTH", 20);
        AccountNumberGenerator.setNumLength(length);
        logger.info("AccountNumberGenerator configuration installed");
    }

    // 40002 index of BANK_ACCOUNT_ID_INDEX is default index if env not loaded or error of keys
    // other indexes - env loaded
    public void configBankAccountNumberGenerator() {
        logger.info("Installing BankAccountNumberGenerator configuration");
        int length = getInt("BANK_NUMBER_GENERATOR_LENGTH", 25);
        String index = get("BANK_ACCOUNT_ID_INDEX", 5,"40002");
        BankNumberGenerator.setLENGTH(length);
        BankNumberGenerator.setINDEX(index);

        Map<CurrencyType, String> currencies = getByPrefixShort("BANK_NUMBER_GENERATOR_").entrySet().stream().filter(
                entry -> {
                    try {
                        CurrencyType.valueOf(entry.getKey());
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }).collect(Collectors.toMap(entry -> CurrencyType.valueOf(entry.getKey()), Map.Entry::getValue));

        // to trace
//        logger.info("Checking currencies -> ");
//        for(Map.Entry<CurrencyType, String> entry: currencies.entrySet()) {
//            logger.info("key: {} == value: {}", entry.getKey(), entry.getValue());
//        }

        BankNumberGenerator.setCurrencyINDEX(currencies);
        logger.info("BankAccountNumberGenerator configuration installed");


    }


    public Map<String, String> getConfiguration() {
        return new HashMap<>(configuration);
    }

    public Set<String> getConfigurationKeys() {
        return configuration.keySet();
    }

    public String get(String key) {
        return configuration.get(key);
    }

    public String get(String key, String defaultValue) {
        return configuration.getOrDefault(key, defaultValue);
    }

    public String get(String key, long expectedLength, String defaultValue) {
        String value = configuration.get(key);
        return (value != null && value.length() == expectedLength) ? value : defaultValue;
    }

    public Optional<String> getOptional(String key) {
        return Optional.ofNullable(configuration.get(key));
    }

    public int getInt(String key) {
        String value = get(key);
        if (value == null) throw new EnvironmentException(String.format("Key \"%s\" wasn't found", key));
        return Integer.parseInt(value);
    }

    public int getInt(String key, int defaultValue) {
        try {
            String value = get(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            String value = get(key);
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;
        return "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value);
    }

    public Map<String, String> getByPrefix(String prefix) {
        return configuration.entrySet().stream().filter(entry ->
                entry.getKey().startsWith(prefix)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, String> getByPrefixShort(String prefix) {
        return configuration.entrySet().stream().filter(entry ->
                entry.getKey().startsWith(prefix)).collect(Collectors.toMap(entry ->
                entry.getKey().substring(prefix.length()), Map.Entry::getValue));
    }

}
