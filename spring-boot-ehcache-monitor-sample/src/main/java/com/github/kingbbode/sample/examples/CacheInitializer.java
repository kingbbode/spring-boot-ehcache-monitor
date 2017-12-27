package com.github.kingbbode.sample.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class CacheInitializer {

    private final Cache1 cache1;

    private final Cache2 cache2;

    private final Cache3 cache3;

    @Autowired
    public CacheInitializer(Cache1 cache1, Cache2 cache2, Cache3 cache3) {
        this.cache1 = cache1;
        this.cache2 = cache2;
        this.cache3 = cache3;
    }
    
    @Scheduled(fixedDelay = 5000)
    public void init() {
        IntStream.range(0, 100)
                .mapToObj(String::valueOf)
                .forEach(cache1::test);
        IntStream.range(0, 100)
                .mapToObj(String::valueOf)
                .forEach(cache1::test);
        IntStream.range(0, 60)
                .mapToObj(String::valueOf)
                .forEach(cache1::test);
        IntStream.range(0, 80)
                .mapToObj(String::valueOf)
                .forEach(cache2::test);
        IntStream.range(0, 400)
                .mapToObj(String::valueOf)
                .forEach(cache3::test);
        IntStream.range(0, 100)
                .mapToObj(String::valueOf)
                .forEach(cache3::test);
        IntStream.range(0, 10)
                .mapToObj(String::valueOf)
                .forEach(cache3::test);
    }
}
