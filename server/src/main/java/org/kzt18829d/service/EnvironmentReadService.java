package org.kzt18829d.service;

import org.kzt18829d.exception.EnvironmentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class EnvironmentReadService {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentReadService.class);
    private static final Object lock = new Object();

    private static final Map<String, String> configuration = new ConcurrentHashMap<>();
    private static final List<Path> envFilePaths = new CopyOnWriteArrayList<>();

    private static final Pattern ENV_LINE = Pattern.compile("^\\s*([A-Za-z_][A-Za-z0-9_]*)=\\s*(.*)$");

    private volatile boolean allowThrowOnMissingValue = false;
    private final AtomicInteger TotalLoadCount = new AtomicInteger(0);
    private final AtomicInteger SuccessLoadCount = new AtomicInteger(0);
    private final AtomicInteger ErrorLoadCount = new AtomicInteger(0);


    private static final class InstanceHolder {
        private static final EnvironmentReadService instance = new EnvironmentReadService();
    }

    private EnvironmentReadService() {}

    public static EnvironmentReadService getInstance() {
        return InstanceHolder.instance;
    }

    public void setAllowThrowOnMissingValue(boolean allowThrowOnMissingValue) {
        this.allowThrowOnMissingValue = allowThrowOnMissingValue;
    }

    public EnvironmentReadService addFile(String filePath) {
        Path path = Paths.get(filePath);
        synchronized (lock) {
            if (!envFilePaths.contains(path))
                envFilePaths.add(path);
            else logger.warn("File \"{}\" has already been added", filePath);
        }
        return this;
    }

    public EnvironmentReadService addFiles(String... filePaths) {
        for (var filePath: filePaths) addFile(filePath);
        return this;
    }

    public EnvironmentReadService load() throws IOException {
        synchronized (lock) {
            TotalLoadCount.set(0);
            SuccessLoadCount.set(0);
            ErrorLoadCount.set(0);
            logger.info("Staring load environments from envFilePaths...");
            if (envFilePaths.isEmpty()) {
                logger.error("envFilePath is empty. Cannot load environments");
                throw new EnvironmentException("envFilePath is empty. Cannot load environments. Use 'addFile' or 'addFiles' before that method");
            }
            for (Path envFile : envFilePaths) load(envFile);
            logger.info("Statistic of load: Total -> {}, Success -> {}, Errors -> {}", TotalLoadCount, SuccessLoadCount, ErrorLoadCount);
        }
        return this;
    }

    private void load(Path envFile) throws IOException {
        logger.info("Starting load environment from {}", envFile.getFileName());
        if (!Files.exists(envFile)) {
            logger.error("File \"{}\" wasn't exists", envFile.getFileName());
            File f = new File(envFile.toString());
//            logger.info("Created file in {}", f.getPath());
//            logger.info("Created file in(a) {}", f.getAbsolutePath());
            return;
        }
        List<String> lines = Files.readAllLines(envFile);

        int lineNum = 0;
        int success = 0;
        int errors = 0;
        for (String line: lines) {
            lineNum++;
            try {
                parseLine(line);
                success++;
            } catch (Exception e) {
                logger.error("Error in \"{}\" - Line {}: {}", envFile.getFileName(), lineNum, e.getMessage());
                errors++;
            }
        }
        TotalLoadCount.addAndGet(lineNum);
        SuccessLoadCount.addAndGet(success);
        ErrorLoadCount.addAndGet(errors);
        logger.info("Statistic of environments in {} { Success: {}; Errors: {}; Lines {}}", envFile.getFileName(), success, errors, lineNum);
    }

    private void parseLine(String line) {
        if (line.isEmpty() || line.startsWith("#")) return;
        line = splitComment(line);

        int equalIndex = line.indexOf('=');
        if (equalIndex == -1) return;

        Matcher matcher = ENV_LINE.matcher(line);
        if (matcher.matches()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            value = processValue(value);
            if (allowThrowOnMissingValue && value.isEmpty())
                throw new EnvironmentException(String.format("Null value of key \"%s\"", key));

            configuration.put(key, value);
        }
    }

    private String splitComment(String line) {
        boolean quotes = false;
        StringBuilder result = new StringBuilder();
        char lastSymbol = line.charAt(0);
        for(char symbol: line.toCharArray()) {
            if (symbol == '"' && lastSymbol != '\\') quotes = !quotes;
            if (symbol == '#' && !quotes) break;
            result.append(symbol);
            lastSymbol = symbol;
        }
        return result.toString();
    }

    private String removeQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char second = value.charAt(value.length() - 1);
            if ((first == '"' && second == '"') || (first == '\'' && second == '\''))
                return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private String processEscapeSequence(String value) {
        return value
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\r", "\r")
                .replace("\\\"", "\"")
                .replace("\\'", "'")
                .replace("\\\\", "\\");
    }

    private String processValue(String value) {
        if (value.isEmpty()) return value;
        value = removeQuotes(value);
        value = processEscapeSequence(value);
        return value;
    }


    public String get(String key) {
        return configuration.get(key);
    }

    public String get(String key, String defaultValue) {
        return configuration.getOrDefault(key, defaultValue);
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
        return configuration.entrySet().stream().filter(entry -> entry.getKey().startsWith(prefix)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean containsKey(String key) {
        return configuration.containsKey(key);
    }

    public Map<String, String> getAll() {
        return new HashMap<>(configuration);
    }

    public Set<String> getConfigurationKeys() {
        return configuration.keySet();
    }

    public void set(String key, String value) {
        configuration.put(key, value);
    }

    public void printAll() {
        for (Map.Entry<String, String> entry: configuration.entrySet()) {
            System.out.printf("%s            =    %s%n", entry.getKey(), entry.getValue());
        }
    }
}
