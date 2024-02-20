package com.github.gustavoflor.rinha;

import com.github.gustavoflor.rinha.container.ContainerConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@ActiveProfiles("test")
@Testcontainers
@ExtendWith(SpringExtension.class)
@Import(ContainerConfig.class)
public abstract class CoreTest {

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
