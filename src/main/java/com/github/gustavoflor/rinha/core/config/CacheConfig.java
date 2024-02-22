package com.github.gustavoflor.rinha.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfig {

    public static final String GET_STATEMENT_KEY = "get-statement";

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(final ObjectMapper objectMapper) {
        final var cacheObjectMapper = objectMapper.copy()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), EVERYTHING, PROPERTY);
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(60))
            .disableCachingNullValues()
            .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer(cacheObjectMapper)));
    }

}
