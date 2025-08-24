import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.kzt18829d.service.EnvironmentReadService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnvironmentReadServiceIntegrationTest {

    @TempDir
    Path tempDir;

    private EnvironmentReadService service;

    @BeforeEach
    void setUp() {
        service = EnvironmentReadService.getInstance();
    }

    @Test
    @Order(1)
    @DisplayName("Полный цикл работы с реальными ENV файлами")
    void testFullWorkflow() throws IOException {
        // Создаем файлы конфигурации как в реальном проекте
        Path dbConfig = tempDir.resolve("database.env");
        Files.write(dbConfig, """
            # Database Configuration
            DB_HOST=localhost
            DB_PORT=5432
            DB_NAME=myapp
            DB_USER=admin
            DB_PASSWORD="complex_password!@#$%"
            DB_SSL=true
            DB_TIMEOUT=30
            
            # Connection Pool
            DB_POOL_MIN=5
            DB_POOL_MAX=20
            """.getBytes());

        Path appConfig = tempDir.resolve("application.env");
        Files.write(appConfig, """
            # Application Settings
            APP_NAME="My Application"
            APP_VERSION=1.2.3
            APP_DEBUG=false
            APP_LOG_LEVEL=INFO
            
            # External Services
            API_KEY="sk-1234567890abcdef"
            API_URL="https://api.example.com/v1"
            API_TIMEOUT=5000
            """.getBytes());

        Path secretsConfig = tempDir.resolve("secrets.env");
        Files.write(secretsConfig, """
            # Secrets (would be encrypted in real app)
            JWT_SECRET="super-secret-jwt-key-that-should-be-long"
            ENCRYPTION_KEY="AES-256-key-for-encryption"
            OAUTH_CLIENT_SECRET="oauth-client-secret-from-provider"
            """.getBytes());

        // Загружаем все конфигурации
        service.addFiles(
                dbConfig.toString(),
                appConfig.toString(),
                secretsConfig.toString()
        ).load();

        // Проверяем DB конфигурацию
        assertEquals("localhost", service.get("DB_HOST"));
        assertEquals(5432, service.getInt("DB_PORT"));
        assertEquals("complex_password!@#$%", service.get("DB_PASSWORD"));
        assertTrue(service.getBoolean("DB_SSL", false));

        // Проверяем пулы соединений
        assertEquals(5, service.getInt("DB_POOL_MIN"));
        assertEquals(20, service.getInt("DB_POOL_MAX"));

        // Проверяем приложение
        assertEquals("My Application", service.get("APP_NAME"));
        assertEquals("1.2.3", service.get("APP_VERSION"));
        assertFalse(service.getBoolean("APP_DEBUG", true));

        // Проверяем внешние сервисы
        assertEquals(5000, service.getInt("API_TIMEOUT"));
        assertTrue(service.get("API_URL").startsWith("https://"));

        // Проверяем группировку по префиксам
        var dbSettings = service.getByPrefix("DB_");
        assertEquals(7, dbSettings.size());

        var appSettings = service.getByPrefix("APP_");
        assertEquals(4, appSettings.size());

        // Проверяем секреты
        assertNotNull(service.get("JWT_SECRET"));
        assertTrue(service.get("JWT_SECRET").length() > 20);
    }

    @Test
    @Order(2)
    @DisplayName("Конфликты и переопределение значений")
    void testConfigOverrides() throws IOException {
        // Base config
        Path baseConfig = tempDir.resolve("base.env");
        Files.write(baseConfig, """
            ENVIRONMENT=development
            DEBUG=true
            LOG_LEVEL=DEBUG
            DATABASE_URL=localhost:5432
            CACHE_TTL=3600
            """.getBytes());

        // Production overrides
        Path prodConfig = tempDir.resolve("production.env");
        Files.write(prodConfig, """
            ENVIRONMENT=production
            DEBUG=false
            LOG_LEVEL=ERROR
            DATABASE_URL=prod-db.company.com:5432
            # CACHE_TTL остается из base
            NEW_PROD_SETTING=enabled
            """.getBytes());

        service.addFiles(baseConfig.toString(), prodConfig.toString()).load();

        // Production должен переопределить base
        assertEquals("production", service.get("ENVIRONMENT"));
        assertEquals(false, service.getBoolean("DEBUG", true));
        assertEquals("ERROR", service.get("LOG_LEVEL"));
        assertEquals("prod-db.company.com:5432", service.get("DATABASE_URL"));

        // Значение из base должно остаться
        assertEquals(3600, service.getInt("CACHE_TTL"));

        // Новое значение из prod
        assertEquals("enabled", service.get("NEW_PROD_SETTING"));
    }

    @Test
    @Order(3)
    @DisplayName("Симуляция hot reload в многопоточной среде")
    void testHotReloadMultiThreaded() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Path configFile = tempDir.resolve("hot-reload.env");

        // Начальная конфигурация
        Files.write(configFile, """
            FEATURE_FLAG=false
            MAX_CONNECTIONS=10
            TIMEOUT=1000
            """.getBytes());

        service.addFile(configFile.toString()).load();

        // Запускаем потоки, которые читают конфигурацию
        ExecutorService readers = Executors.newFixedThreadPool(5);
        CountDownLatch readersLatch = new CountDownLatch(5);
        boolean continueReading = true;

        for (int i = 0; i < 5; i++) {
            boolean finalContinueReading = continueReading;
            readers.submit(() -> {
                try {
                    while (finalContinueReading) {
                        // Эмулируем чтение конфигурации в рабочих потоках
                        boolean flag = service.getBoolean("FEATURE_FLAG", false);
                        int connections = service.getInt("MAX_CONNECTIONS", 5);
                        int timeout = service.getInt("TIMEOUT", 500);

                        // Проверяем консистентность
                        assertTrue(connections >= 5, "Connections должно быть >= 5");
                        assertTrue(timeout >= 500, "Timeout должен быть >= 500");

                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    readersLatch.countDown();
                }
            });
        }

        // Даем потокам поработать
        Thread.sleep(100);

        // Обновляем конфигурацию (hot reload)
        Files.write(configFile, """
            FEATURE_FLAG=true
            MAX_CONNECTIONS=50
            TIMEOUT=5000
            NEW_SETTING=added_during_reload
            """.getBytes());

        // Перезагружаем в отдельном потоке
        ExecutorService reloader = Executors.newSingleThreadExecutor();
        Future<?> reloadTask = reloader.submit(() -> {
            try {
                service.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Ждем завершения перезагрузки
        reloadTask.get(5, TimeUnit.SECONDS);

        // Проверяем новые значения
        assertTrue(service.getBoolean("FEATURE_FLAG", false));
        assertEquals(50, service.getInt("MAX_CONNECTIONS"));
        assertEquals(5000, service.getInt("TIMEOUT"));
        assertEquals("added_during_reload", service.get("NEW_SETTING"));

        // Останавливаем читающие потоки
        continueReading = false;
        assertTrue(readersLatch.await(5, TimeUnit.SECONDS));

        readers.shutdown();
        reloader.shutdown();
    }

    @Test
    @Order(4)
    @DisplayName("Обработка больших файлов конфигурации")
    void testLargeConfigFiles() throws IOException {
        Path largeConfig = tempDir.resolve("large-config.env");
        StringBuilder content = new StringBuilder();

        // Генерируем большой файл конфигурации
        content.append("# Large configuration file with many settings\n");
        for (int i = 0; i < 1000; i++) {
            content.append(String.format("SETTING_%04d=value_%04d\n", i, i));
            if (i % 100 == 0) {
                content.append(String.format("# Section %d\n", i / 100));
            }
        }

        // Добавляем различные типы значений
        content.append("""
            # Special values
            LONG_STRING="This is a very long string value that contains many words and should test the parser's ability to handle lengthy content without issues"
            MULTILINE_LIKE="Line 1\\nLine 2\\nLine 3\\nThis simulates multiline content"
            COMPLEX_JSON_LIKE='{"key": "value", "number": 123, "boolean": true, "array": [1,2,3]}'
            URL_WITH_PARAMS="https://example.com/api?param1=value1&param2=value2&param3=value%20with%20spaces"
            """);

        Files.write(largeConfig, content.toString().getBytes());

        long startTime = System.currentTimeMillis();
        service.addFile(largeConfig.toString()).load();
        long endTime = System.currentTimeMillis();

        // Производительность должна быть приемлемой
        assertTrue(endTime - startTime < 5000, "Загрузка большого файла должна занимать < 5 секунд");

        // Проверяем, что все значения загружены
        for (int i = 0; i < 1000; i++) {
            String expected = String.format("value_%04d", i);
            String key = String.format("SETTING_%04d", i);
            assertEquals(expected, service.get(key), "Значение " + key + " должно быть корректным");
        }

        // Проверяем специальные значения
        assertTrue(service.get("LONG_STRING").length() > 100);
        assertTrue(service.get("MULTILINE_LIKE").contains("\n"));
        assertTrue(service.get("COMPLEX_JSON_LIKE").contains("{"));
        assertTrue(service.get("URL_WITH_PARAMS").startsWith("https://"));

        // Проверяем общее количество настроек
        assertTrue(service.getAll().size() >= 1000, "Должно быть загружено >= 1000 настроек");
    }

    @Test
    @Order(5)
    @DisplayName("Реальный пример конфигурации микросервиса")
    void testMicroserviceConfig() throws IOException {
        Path microserviceConfig = tempDir.resolve("microservice.env");
        Files.write(microserviceConfig, """
            # Microservice Configuration
            
            # Service Info
            SERVICE_NAME="user-management-service"
            SERVICE_VERSION="2.1.0"
            SERVICE_PORT=8080
            SERVICE_CONTEXT_PATH="/api/v1"
            
            # Database
            DATABASE_URL="jdbc:postgresql://db.internal:5432/userdb"
            DATABASE_USERNAME="service_user"
            DATABASE_PASSWORD="secure_db_password_123!"
            DATABASE_MAX_POOL_SIZE=20
            DATABASE_MIN_POOL_SIZE=2
            DATABASE_CONNECTION_TIMEOUT=30000
            
            # Redis Cache
            REDIS_HOST="cache.internal"
            REDIS_PORT=6379
            REDIS_PASSWORD=""
            REDIS_DATABASE=0
            REDIS_TIMEOUT=2000
            
            # External APIs
            USER_API_URL="https://api.users.company.com/v2"
            USER_API_KEY="uk_1234567890abcdef1234567890abcdef"
            USER_API_TIMEOUT=10000
            USER_API_RETRIES=3
            
            NOTIFICATION_API_URL="https://notifications.company.com/send"
            NOTIFICATION_API_KEY="nk_abcdef1234567890abcdef1234567890"
            
            # Security
            JWT_SECRET="jwt-secret-key-that-should-be-very-long-and-random-in-production"
            JWT_EXPIRATION=86400
            CORS_ALLOWED_ORIGINS="https://app.company.com,https://admin.company.com"
            RATE_LIMIT_REQUESTS=1000
            RATE_LIMIT_WINDOW=3600
            
            # Monitoring & Logging
            LOG_LEVEL=INFO
            LOG_FORMAT=JSON
            METRICS_ENABLED=true
            HEALTH_CHECK_PATH="/health"
            PROMETHEUS_PATH="/metrics"
            
            # Feature Flags
            FEATURE_NEW_USER_VALIDATION=true
            FEATURE_ADVANCED_SEARCH=false
            FEATURE_BULK_OPERATIONS=true
            FEATURE_AUDIT_LOGGING=true
            
            # Performance Tuning
            THREAD_POOL_SIZE=50
            QUEUE_CAPACITY=10000
            CACHE_TTL=3600
            BATCH_SIZE=100
            """.getBytes());

        service.addFile(microserviceConfig.toString()).load();

        // Service configuration
        assertEquals("user-management-service", service.get("SERVICE_NAME"));
        assertEquals(8080, service.getInt("SERVICE_PORT"));
        assertEquals("2.1.0", service.get("SERVICE_VERSION"));

        // Database settings
        assertTrue(service.get("DATABASE_URL").contains("postgresql"));
        assertEquals(20, service.getInt("DATABASE_MAX_POOL_SIZE"));
        assertEquals(30000, service.getInt("DATABASE_CONNECTION_TIMEOUT"));

        // External APIs
        assertEquals(3, service.getInt("USER_API_RETRIES"));
        assertTrue(service.get("USER_API_KEY").startsWith("uk_"));
        assertTrue(service.get("NOTIFICATION_API_KEY").startsWith("nk_"));

        // Security
        assertTrue(service.get("JWT_SECRET").length() > 50);
        assertEquals(86400, service.getInt("JWT_EXPIRATION"));
        assertTrue(service.get("CORS_ALLOWED_ORIGINS").contains(","));

        // Feature flags
        assertTrue(service.getBoolean("FEATURE_NEW_USER_VALIDATION", false));
        assertFalse(service.getBoolean("FEATURE_ADVANCED_SEARCH", true));
        assertTrue(service.getBoolean("FEATURE_AUDIT_LOGGING", false));

        // Performance
        assertEquals(50, service.getInt("THREAD_POOL_SIZE"));
        assertEquals(100, service.getInt("BATCH_SIZE"));

        // Group testing
        var featureFlags = service.getByPrefix("FEATURE_");
        assertEquals(4, featureFlags.size());

        var dbSettings = service.getByPrefix("DATABASE_");
        assertEquals(6, dbSettings.size());

        var apiSettings = service.getByPrefix("API_");
        assertTrue(apiSettings.size() >= 4);

        // Comprehensive check
        assertTrue(service.getAll().size() > 30, "Microservice config should have 30+ settings");
    }
}