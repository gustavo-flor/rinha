package com.github.gustavoflor.rinha;

import com.github.gustavoflor.rinha.core.repository.CustomerRepository;
import com.github.gustavoflor.rinha.core.repository.TransferRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@ActiveProfiles("test")
@Testcontainers
@ExtendWith(SpringExtension.class)
public abstract class CoreTest {

    @Container
    protected static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2.6")
        .withExposedPorts(6379);

    @SpyBean
    protected CustomerRepository customerRepository;

    @SpyBean
    protected TransferRepository transferRepository;

    @AfterAll
    static void afterAll() {
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
//        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgresContainer::getUsername);
//        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    protected void doSyncAndConcurrently(int threadCount, Consumer<String> operation) throws InterruptedException {
        final var startLatch = new CountDownLatch(1);
        final var endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            String threadName = "thread-" + i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    operation.accept(threadName);
                } catch (Exception e) {
                    System.err.printf("Error while executing operation %s: %s%n", threadName, e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        startLatch.countDown();
        endLatch.await();
    }

}
