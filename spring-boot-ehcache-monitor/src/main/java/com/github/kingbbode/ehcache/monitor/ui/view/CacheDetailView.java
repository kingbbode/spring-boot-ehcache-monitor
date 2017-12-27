package com.github.kingbbode.ehcache.monitor.ui.view;

import com.github.kingbbode.ehcache.monitor.ui.view.component.CacheDetailComponent;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * Created by YG-MAC on 2017. 12. 18..
 */
@SpringView(name = "detail")
public class CacheDetailView extends CacheDetailComponent {
    public CacheDetailView(CacheManager cacheManager) {
        super(((EhCacheCacheManager) cacheManager).getCacheManager());
    }
}
