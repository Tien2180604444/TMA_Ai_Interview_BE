package com.example.interview.config;

import com.example.interview.constaint.CacheName;  // sửa typo: constraint → constaint (kiểm tra lại tên package/class)
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@AllArgsConstructor
public class RedisConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration(ObjectMapper objectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(12))                     // TTL mặc định cho các cache không custom
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJacksonJsonRedisSerializer(objectMapper)));
    }

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            RedisCacheConfiguration defaultCacheConfig) {

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(CacheName.JOB_GROUPS,
                defaultCacheConfig.entryTtl(Duration.ofDays(8)));

        cacheConfigurations.put(CacheName.PROFESSIONAL_POSITIONS,
                defaultCacheConfig.entryTtl(Duration.ofDays(8)));

        cacheConfigurations.put(CacheName.DIFFICULT_LEVELS,
                defaultCacheConfig.entryTtl(Duration.ofDays(30)));

        cacheConfigurations.put(CacheName.QUESTION_TYPES,
                defaultCacheConfig.entryTtl(Duration.ofHours(6)));

        cacheConfigurations.put(CacheName.EVALUATION_CRITERIA,
                defaultCacheConfig.entryTtl(Duration.ofDays(15)));


        return RedisCacheManager.builder(redisConnectionFactory)
                .initialCacheNames(cacheConfigurations.keySet())
                .withInitialCacheConfigurations(cacheConfigurations)
                .cacheDefaults(defaultCacheConfig)
                .build();
    }
}