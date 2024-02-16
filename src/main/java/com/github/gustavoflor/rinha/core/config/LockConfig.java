package com.github.gustavoflor.rinha.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

import static org.springframework.integration.redis.util.RedisLockRegistry.RedisLockType.PUB_SUB_LOCK;

@Configuration
@RequiredArgsConstructor
public class LockConfig {

    private static final String REGISTRY_KEY = "lock";

    private final RedisConnectionFactory redisConnectionFactory;

    @Bean(destroyMethod = "destroy")
    public RedisLockRegistry redisLockRegistry() {
        final var lockRegistry = new RedisLockRegistry(redisConnectionFactory, REGISTRY_KEY);
        lockRegistry.setRedisLockType(PUB_SUB_LOCK);
        return lockRegistry;
    }

}
