package com.github.kingbbode.ehcache.monitor;

import com.github.kingbbode.ehcache.monitor.component.CacheHistoryStore;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.terracotta.statistics.archive.Timestamped;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CacheHistoryStoreTest {

    @Mock
    private EhCacheCacheManager springCacheManager;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private StatisticsGateway statisticsGateway;

    @Mock
    private ExtendedStatistics.Result result;
    @Mock
    private ExtendedStatistics.Statistic<Long> statistic;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void EHCACHE_통계_데이터가_분단위로_GROUP_되고_최종시간_기준으로_값이_갱신되는가() {
        when(this.springCacheManager.getCacheManager()).thenReturn(cacheManager);
        when(this.cacheManager.getCacheNames()).thenReturn(new String[]{"Cache1"});
        Cache cache = new MockCache(this.statisticsGateway, "Cache1", 100, false, false, 0L, 0L);
        when(this.cacheManager.getCache("Cache1")).thenReturn(cache);
        when(statisticsGateway.cacheHitOperation()).thenReturn(result);
        when(result.count()).thenReturn(statistic);
        when(statistic.history()).thenReturn(
                Arrays.asList(
                        new MockTimestamped(2L, 10000L),
                        new MockTimestamped(1L, 0L),
                        new MockTimestamped(3L, 50000L),
                        new MockTimestamped(7L, 90000L),
                        new MockTimestamped(5L, 70000L),
                        new MockTimestamped(6L, 80000L),
                        new MockTimestamped(4L, 60000L),
                        new MockTimestamped(8L, 120000L)
                )
        );
        CacheHistoryStore cacheHistoryStore = new CacheHistoryStore(this.springCacheManager);
        cacheHistoryStore.fetch();
        assertEquals(3, cacheHistoryStore.get("Cache1").size());
        cacheHistoryStore.get("Cache1").forEach(timestamped -> {
            if (timestamped.getTimestamp() < 60000L) {
                assertTrue(3L == timestamped.getSample());
            } else if (timestamped.getTimestamp() < 120000L) {
                assertTrue(7L == timestamped.getSample());
            } else {
                assertTrue(8L == timestamped.getSample());
            }
        });
    }

    private class MockCache extends Cache {

        private final StatisticsGateway statisticsGateway;

        MockCache(StatisticsGateway statisticsGateway, String name, int maxElementsInMemory, boolean overflowToDisk, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds) {
            super(name, maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
            this.statisticsGateway = statisticsGateway;
        }

        @Override
        public StatisticsGateway getStatistics() throws IllegalStateException {
            return statisticsGateway;
        }
    }

    private class MockTimestamped implements Timestamped<Long> {
        private Long sample;
        private Long timestamp;

        MockTimestamped(Long sample, Long timestamp) {
            this.sample = sample;
            this.timestamp = timestamp;
        }

        @Override
        public Long getSample() {
            return sample;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }
    }
}
