package com.github.kingbbode.ehcache.monitor.ui.view;

import com.github.kingbbode.ehcache.monitor.component.CacheHistoryStore;
import com.github.kingbbode.ehcache.monitor.ui.view.component.CacheInfoComponent;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

@SpringView(name = "")
public class DashboardView extends CacheInfoComponent {
    @Autowired
    public DashboardView(CacheManager springCacheManager, CacheHistoryStore store) {
        super(((EhCacheCacheManager) springCacheManager).getCacheManager(), store);
    }
}
