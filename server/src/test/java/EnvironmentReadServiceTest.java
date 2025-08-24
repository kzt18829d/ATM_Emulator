
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.kzt18829d.exception.EnvironmentException;
import org.kzt18829d.service.EnvironmentReadService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnvironmentReadServiceTest {

    @TempDir
    Path tempDir;

    private EnvironmentReadService service;

    @BeforeEach
    void setUp() {
        service = EnvironmentReadService.getInstance();
        // Очищаем конфигурацию перед каждым тестом
        service.getAll().keySet().forEach(key -> service.set(key, null));
    }

    @Test
    @Order(1)
    @DisplayName("Singleton pattern - один экземпляр")
    void testSingletonPattern() {
        EnvironmentReadService instance1 = EnvironmentReadService.getInstance();
        EnvironmentReadService instance2 = EnvironmentReadService.getInstance();

        assertSame(instance1, instance2, "getInstance должен возвращать один и тот же экземпляр");
    }

    @Test
    @Order(2)
    @DisplayName("Загрузка простого ENV файла")
    void testLoadSimpleEnvFile() throws IOException {
        // Создаем тестовый файл
        Path envFile = tempDir.resolve("test.env");
        Files.write(envFile, """
            # Комментарий
            SIMPLE_KEY=simple_value
            NUMERIC_KEY=123
            BOOLEAN_KEY=true
            
            # Еще один комментарий
            ANOTHER_KEY=another_value
            """.getBytes());

        service.addFile(envFile.toString()).load();

        assertEquals("simple_value", service.get("SIMPLE_KEY"));
        assertEquals("123", service.get("NUMERIC_KEY"));
        assertEquals("true", service.get("BOOLEAN_KEY"));
        assertEquals("another_value", service.get("ANOTHER_KEY"));
    }

    @Test
    @Order(3)
    @DisplayName("Парсинг значений с кавычками")
    void testQuotedValues() throws IOException {
        Path envFile = tempDir.resolve("quoted.env");
        Files.write(envFile, """
            DOUBLE_QUOTED="value with spaces"
            SINGLE_QUOTED='another value'
            MIXED_QUOTES="value with 'inner quotes'"
            ESCAPED_QUOTES="value with \\"escaped\\" quotes"
            NO_QUOTES=plain_value
            """.getBytes());

        service.addFile(envFile.toString()).load();

        assertEquals("value with spaces", service.get("DOUBLE_QUOTED"));
        assertEquals("another value", service.get("SINGLE_QUOTED"));
        assertEquals("value with 'inner quotes'", service.get("MIXED_QUOTES"));
        assertEquals("value with \"escaped\" quotes", service.get("ESCAPED_QUOTES"));
        assertEquals("plain_value", service.get("NO_QUOTES"));
    }

    @Test
    @Order(4)
    @DisplayName("Комментарии внутри значений")
    void testCommentsInValues() throws IOException {
        Path envFile = tempDir.resolve("comments.env");
        Files.write(envFile, """
            # Это комментарий
            KEY_WITH_COMMENT=value # это комментарий в конце
            QUOTED_WITH_HASH="value#with#hash" # это комментарий
            QUOTED_WITH_COMMENT="value with # inside" # комментарий
            PASSWORD="my#secret#password" # секретный пароль
            """.getBytes());

        service.addFile(envFile.toString()).load();

        assertEquals("value", service.get("KEY_WITH_COMMENT"));
        assertEquals("value#with#hash", service.get("QUOTED_WITH_HASH"));
        assertEquals("value with # inside", service.get("QUOTED_WITH_COMMENT"));
        assertEquals("my#secret#password", service.get("PASSWORD"));
    }

    @Test
    @Order(5)
    @DisplayName("Escape последовательности")
    void testEscapeSequences() throws IOException {
        Path envFile = tempDir.resolve("escapes.env");
        Files.write(envFile, """
            NEWLINE="line1\\nline2"
            TAB="col1\\tcol2"
            CARRIAGE_RETURN="line1\\rline2"
            BACKSLASH="path\\\\to\\\\file"
            QUOTES="say \\"hello\\""
            """.getBytes());

        service.addFile(envFile.toString()).load();

        assertEquals("line1\nline2", service.get("NEWLINE"));
        assertEquals("col1\tcol2", service.get("TAB"));
        assertEquals("line1\rline2", service.get("CARRIAGE_RETURN"));
        assertEquals("path\\to\\file", service.get("BACKSLASH"));
        assertEquals("say \"hello\"", service.get("QUOTES"));
    }

    @Test
    @Order(6)
    @DisplayName("Типизированные методы получения")
    void testTypedGetters() throws IOException {
        Path envFile = tempDir.resolve("types.env");
        Files.write(envFile, """
            INT_VALUE=42
            DOUBLE_VALUE=3.14
            BOOLEAN_TRUE=true
            BOOLEAN_FALSE=false
            BOOLEAN_ONE=1
            BOOLEAN_ZERO=0
            """.getBytes());

        service.addFile(envFile.toString()).load();

        // Integers
        assertEquals(42, service.getInt("INT_VALUE"));
        assertEquals(100, service.getInt("NONEXISTENT_INT", 100));

        // Doubles
        assertEquals(3.14, service.getDouble("DOUBLE_VALUE", 0.0), 0.001);
        assertEquals(2.71, service.getDouble("NONEXISTENT_DOUBLE", 2.71), 0.001);

        // Booleans
        assertTrue(service.getBoolean("BOOLEAN_TRUE", false));
        assertFalse(service.getBoolean("BOOLEAN_FALSE", true));
        assertTrue(service.getBoolean("BOOLEAN_ONE", false));
        assertFalse(service.getBoolean("BOOLEAN_ZERO", true));
        assertTrue(service.getBoolean("NONEXISTENT_BOOLEAN", true));
    }

    @Test
    @Order(7)
    @DisplayName("Optional и null values")
    void testOptionalAndNullHandling() throws IOException {
        Path envFile = tempDir.resolve("optional.env");
        Files.write(envFile, """
            EXISTING_KEY=existing_value
            EMPTY_KEY=
            """.getBytes());

        service.addFile(envFile.toString()).load();

        // Optional tests
        Optional<String> existing = service.getOptional("EXISTING_KEY");
        assertTrue(existing.isPresent());
        assertEquals("existing_value", existing.get());

        Optional<String> nonExisting = service.getOptional("NON_EXISTING_KEY");
        assertFalse(nonExisting.isPresent());

        // Null handling
        assertNull(service.get("NON_EXISTING_KEY"));
        assertEquals("default", service.get("NON_EXISTING_KEY", "default"));

        // Empty value
        assertEquals("", service.get("EMPTY_KEY"));
    }

    @Test
    @Order(8)
    @DisplayName("Поиск по префиксу")
    void testGetByPrefix() throws IOException {
        Path envFile = tempDir.resolve("prefix.env");
        Files.write(envFile, """
            DB_HOST=localhost
            DB_PORT=5432
            DB_USER=admin
            API_KEY=secret
            API_URL=https://api.example.com
            OTHER_SETTING=value
            """.getBytes());

        service.addFile(envFile.toString()).load();

        Map<String, String> dbSettings = service.getByPrefix("DB_");
        assertEquals(3, dbSettings.size());
        assertEquals("localhost", dbSettings.get("DB_HOST"));
        assertEquals("5432", dbSettings.get("DB_PORT"));
        assertEquals("admin", dbSettings.get("DB_USER"));

        Map<String, String> apiSettings = service.getByPrefix("API_");
        assertEquals(2, apiSettings.size());
        assertEquals("secret", apiSettings.get("API_KEY"));
    }

    @Test
    @Order(9)
    @DisplayName("Загрузка нескольких файлов")
    void testLoadMultipleFiles() throws IOException {
        // Первый файл
        Path envFile1 = tempDir.resolve("file1.env");
        Files.write(envFile1, """
            KEY1=value1
            SHARED_KEY=from_file1
            """.getBytes());

        // Второй файл
        Path envFile2 = tempDir.resolve("file2.env");
        Files.write(envFile2, """
            KEY2=value2
            SHARED_KEY=from_file2
            """.getBytes());

        service.addFiles(envFile1.toString(), envFile2.toString()).load();

        assertEquals("value1", service.get("KEY1"));
        assertEquals("value2", service.get("KEY2"));
        // Последний файл должен перезаписать значение
        assertEquals("from_file2", service.get("SHARED_KEY"));
    }

    @Test
    @Order(10)
    @DisplayName("Несуществующий файл")
    void testNonExistentFile() {
        // Не должно бросать исключение, только логировать предупреждение
        assertDoesNotThrow(() -> {
            service.addFile("non_existent_file.env").load();
        });
    }

    @Test
    @Order(11)
    @DisplayName("Пустой список файлов")
    void testEmptyFilesList() {
        EnvironmentReadService emptyService = EnvironmentReadService.getInstance();

        EnvironmentException exception = assertThrows(EnvironmentException.class, () -> {
            // Создаем новый инстанс, но не можем из-за singleton
            emptyService.load();
        });

        assertTrue(exception.getMessage().contains("envFilePath is empty"));
    }

    @Test
    @Order(12)
    @DisplayName("Некорректные строки в файле")
    void testMalformedLines() throws IOException {
        Path envFile = tempDir.resolve("malformed.env");
        Files.write(envFile, """
            # Корректные строки
            VALID_KEY=valid_value
            
            # Некорректные строки
            INVALID_LINE_WITHOUT_EQUALS
            =VALUE_WITHOUT_KEY
            123INVALID_KEY_START=value
            
            # Еще корректная строка
            ANOTHER_VALID=another_value
            """.getBytes());

        // Не должно бросать исключение
        assertDoesNotThrow(() -> service.addFile(envFile.toString()).load());

        // Корректные значения должны быть загружены
        assertEquals("valid_value", service.get("VALID_KEY"));
        assertEquals("another_value", service.get("ANOTHER_VALID"));

        // Некорректные значения не должны быть в конфигурации
        assertNull(service.get("INVALID_LINE_WITHOUT_EQUALS"));
        assertNull(service.get(""));
        assertNull(service.get("123INVALID_KEY_START"));
    }

    @Test
    @Order(13)
    @DisplayName("Настройка allowThrowOnMissingValue")
    void testAllowThrowOnMissingValue() throws IOException {
        Path envFile = tempDir.resolve("empty_values.env");
        Files.write(envFile, """
            KEY_WITH_VALUE=some_value
            KEY_WITH_EMPTY_VALUE=
            """.getBytes());

        // Сначала без исключений
        service.setAllowThrowOnMissingValue(false);
        assertDoesNotThrow(() -> service.addFile(envFile.toString()).load());

        // Теперь с исключениями
        service.setAllowThrowOnMissingValue(true);

        Path envFileEmpty = tempDir.resolve("with_empty.env");
        Files.write(envFileEmpty, "EMPTY_KEY=".getBytes());

        assertThrows(EnvironmentException.class, () -> {
            service.addFile(envFileEmpty.toString()).load();
        });
    }

    @Test
    @Order(14)
    @DisplayName("Методы containsKey, getAll, set")
    void testUtilityMethods() throws IOException {
        service.set("MANUAL_KEY", "manual_value");

        assertTrue(service.containsKey("MANUAL_KEY"));
        assertFalse(service.containsKey("NON_EXISTENT_KEY"));

        Map<String, String> allConfig = service.getAll();
        assertTrue(allConfig.containsKey("MANUAL_KEY"));
        assertEquals("manual_value", allConfig.get("MANUAL_KEY"));

        // Изменение возвращенной Map не должно влиять на оригинал
        allConfig.put("NEW_KEY", "new_value");
        assertFalse(service.containsKey("NEW_KEY"));
    }

    @Test
    @Order(15)
    @DisplayName("Метод printAll")
    void testPrintAll() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(outputStream));

            service.set("TEST_KEY1", "test_value1");
            service.set("TEST_KEY2", "test_value2");

            service.printAll();

            String output = outputStream.toString();
            assertTrue(output.contains("TEST_KEY1"));
            assertTrue(output.contains("test_value1"));
            assertTrue(output.contains("TEST_KEY2"));
            assertTrue(output.contains("test_value2"));

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @Order(16)
    @DisplayName("Exception при отсутствующем ключе в getInt")
    void testGetIntException() {
        EnvironmentException exception = assertThrows(EnvironmentException.class, () -> {
            service.getInt("NON_EXISTENT_INT_KEY");
        });

        // Примечание: в коде ошибка - используется {} вместо %s для String.format
        assertTrue(exception.getMessage().contains("NON_EXISTENT_INT_KEY"));
    }

    @Test
    @Order(17)
    @DisplayName("Обработка NumberFormatException в типизированных методах")
    void testNumberFormatExceptionHandling() throws IOException {
        Path envFile = tempDir.resolve("invalid_numbers.env");
        Files.write(envFile, """
            INVALID_INT=not_a_number
            INVALID_DOUBLE=also_not_a_number
            """.getBytes());

        service.addFile(envFile.toString()).load();

        // Должны возвращаться дефолтные значения при ошибке парсинга
        assertEquals(42, service.getInt("INVALID_INT", 42));
        assertEquals(3.14, service.getDouble("INVALID_DOUBLE", 3.14), 0.001);
    }

    @Test
    @Order(18)
    @DisplayName("Базовая многопоточность")
    void testBasicThreadSafety() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Устанавливаем начальные значения
        for (int i = 0; i < operationsPerThread; i++) {
            service.set("KEY_" + i, "initial_value_" + i);
        }

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        String key = "KEY_" + i;
                        String value = "thread_" + threadId + "_value_" + i;

                        // Операции чтения и записи
                        service.set(key, value);
                        String readValue = service.get(key);
                        service.containsKey(key);
                        service.getOptional(key);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "Тест многопоточности не завершился вовремя");
        executor.shutdown();

        // Проверяем, что данные не повреждены
        for (int i = 0; i < operationsPerThread; i++) {
            String value = service.get("KEY_" + i);
            assertNotNull(value, "Значение не должно быть null после многопоточного доступа");
            assertTrue(value.startsWith("thread_") || value.startsWith("initial_"),
                    "Значение должно быть корректным: " + value);
        }
    }

    @Test
    @Order(19)
    @DisplayName("Повторная загрузка (hot reload simulation)")
    void testReload() throws IOException {
        Path envFile = tempDir.resolve("reload.env");

        // Первая загрузка
        Files.write(envFile, "VERSION=1.0".getBytes());
        service.addFile(envFile.toString()).load();
        assertEquals("1.0", service.get("VERSION"));

        // Обновляем файл
        Files.write(envFile, "VERSION=2.0\nNEW_FEATURE=enabled".getBytes());
        service.load(); // Перезагружаем

        assertEquals("2.0", service.get("VERSION"));
        assertEquals("enabled", service.get("NEW_FEATURE"));
    }

    @Test
    @Order(20)
    @DisplayName("Граничные случаи для splitComment")
    void testSplitCommentEdgeCases() throws IOException {
        Path envFile = tempDir.resolve("comment_edge_cases.env");
        Files.write(envFile, """
            # Только комментарий
            KEY1="value with \\"escaped\\" quote and # hash" # comment
            KEY2='single quote with # hash' # another comment
            KEY3=plain_value_with_#_hash # final comment
            KEY4="multiple \\"quotes\\" and # multiple" # hashes
            """.getBytes());

        service.addFile(envFile.toString()).load();

        assertEquals("value with \"escaped\" quote and # hash", service.get("KEY1"));
        assertEquals("single quote with # hash", service.get("KEY2"));
        assertEquals("plain_value_with_#_hash", service.get("KEY3"));
        assertEquals("multiple \"quotes\" and # multiple", service.get("KEY4"));
    }
}