package com.app.recychool.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // 1. Key Serializer 설정 (String)
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // 2. Value Serializer 설정 (JSON - 객체 직렬화/역직렬화)
        // GenericJackson2JsonRedisSerializer는 타입 정보를 함께 저장하여 안전하게 역직렬화합니다.
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // Key를 StringRedisSerializer로 설정
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                // Value를 GenericJackson2JsonRedisSerializer로 설정 (가장 중요!)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(Duration.ofMinutes(10))  // 캐시 TTL 설정
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}