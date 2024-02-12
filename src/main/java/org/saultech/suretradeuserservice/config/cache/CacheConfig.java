package org.saultech.suretradeuserservice.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration())
                .cacheWriter(redisCacheWriter(redisConnectionFactory))
                .build();
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration redisCacheConfiguration() {
        return org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(java.time.Duration.ofMinutes(5));
    }

    // private org.springframework.data.redis.cache.RedisCacheWriter redisCacheWriter() {
    private org.springframework.data.redis.cache.RedisCacheWriter redisCacheWriter(RedisConnectionFactory redisConnectionFactory) {
        return org.springframework.data.redis.cache.RedisCacheWriter
                .lockingRedisCacheWriter(redisConnectionFactory);
    }

}
