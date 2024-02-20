package com.github.gustavoflor.rinha.container;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:16.2-alpine");
    }

    @Bean
    public RedisContainer<?> redisContainer(final DynamicPropertyRegistry registry) {
        final var container = new RedisContainer<>("redis:7.2.4-alpine");
        registry.add("spring.data.redis.host", container::getHost);
        registry.add("spring.data.redis.port", container::getPort);
        return container;
    }

}
