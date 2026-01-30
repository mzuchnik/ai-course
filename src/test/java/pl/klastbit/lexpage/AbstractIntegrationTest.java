package pl.klastbit.lexpage;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests using TestContainers.
 * <p>
 * Provides a PostgreSQL TestContainer that is shared across all integration tests.
 * The container is started once per test class and reused for all test methods.
 * <p>
 * Usage: Extend this class in your integration tests:
 * <pre>
 * {@code
 * @SpringBootTest
 * class MyIntegrationTest extends AbstractIntegrationTest {
 *     // Your tests here
 * }
 * }
 * </pre>
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

    /**
     * PostgreSQL TestContainer instance.
     * <p>
     * Uses the latest PostgreSQL 16 image.
     * The container is started once and shared across all tests in the class.
     */
    @Container
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("lexpage_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    /**
     * Dynamically configure Spring Boot properties to use TestContainer database.
     * <p>
     * This method is called before the Spring context is created and configures:
     * - JDBC URL pointing to the TestContainer
     * - Database username and password
     * - Liquibase configuration
     *
     * @param registry Spring's dynamic property registry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.liquibase.enabled", () -> true);
    }
}
