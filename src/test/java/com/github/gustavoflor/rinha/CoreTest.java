package com.github.gustavoflor.rinha;

import com.github.gustavoflor.rinha.container.RedisContainer;
import com.github.gustavoflor.rinha.core.repository.CustomerRepository;
import com.github.gustavoflor.rinha.core.repository.TransferRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ActiveProfiles("test")
@Testcontainers
@ExtendWith(SpringExtension.class)
public abstract class CoreTest {

    @Container
    protected static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.2-alpine");

    @Container
    protected static RedisContainer<?> redisContainer = new RedisContainer<>("redis:7.2.4-alpine");

    @SpyBean
    protected CustomerRepository customerRepository;

    @SpyBean
    protected TransferRepository transferRepository;

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
        redisContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
        redisContainer.stop();
    }

    @AfterEach
    void afterEach() {
        Mockito.reset(customerRepository, transferRepository);
        customerRepository.deleteAll();
        transferRepository.deleteAll();
    }

    @DynamicPropertySource
    public static  void overrideProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getPort);
    }

    protected void doSyncAndConcurrently(int threadCount, Consumer<Integer> operation) throws InterruptedException {
        final var startLatch = new CountDownLatch(1);
        final var endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    operation.accept(index);
                } catch (Exception e) {
                    System.err.printf("Error while executing operation on index = [%s]: %s%n", index, e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        startLatch.countDown();
        endLatch.await();
    }

}
