package com.github.kingbbode.sample.examples;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@CacheConfig(cacheNames = "Cache1")
public class Cache1 {
    @Cacheable(key = "#key")
    public Map<String, String> test(String key) {
        Map<String, String> test = new HashMap<>();
        test.put("1", key);
        test.put("9", key);
        test.put("8", key);
        test.put("7", key);
        test.put("6", key);
        test.put("5", key);
        test.put("4", key);
        test.put("3", key);
        test.put("2", key);
        test.put("10", key);
        test.put("19", key);
        test.put("18", key);
        test.put("17", key);
        test.put("16", key);
        test.put("15", key);
        test.put("14", key);
        test.put("13", key);
        test.put("121", key);
        test.put("122", key);
        test.put("123", key);
        test.put("124", key);
        test.put("125", key);
        test.put("126", key);
        test.put("127", key);
        test.put("128", key);
        test.put("129", key);
        test.put("12", key);
        return test;
        //return Collections.singletonMap(key, key);
    }
}
