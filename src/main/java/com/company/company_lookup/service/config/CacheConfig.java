package com.company.company_lookup.service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * This class configures a Caffeine-based cache manager for storing verification data.
 * It uses properties defined in the application configuration to determine the cache size and expiration time.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String VERIFICATION_CACHE_NAME = "verificationCache";

    @Value("${cache.verification.maxSize}")
    private int maxSize;

    @Value("${cache.verification.expireAfterWriteInHours}")
    private int expireAfterWriteInHours;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(VERIFICATION_CACHE_NAME);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWriteInHours, TimeUnit.HOURS)
        );
        return cacheManager;
    }
}