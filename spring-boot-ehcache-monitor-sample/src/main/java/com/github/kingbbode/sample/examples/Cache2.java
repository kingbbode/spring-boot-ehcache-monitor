package com.github.kingbbode.sample.examples;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "Cache2")
public class Cache2 {
    @Cacheable(key = "#key")
    public String test(String key) {
        return key;
    }
}
