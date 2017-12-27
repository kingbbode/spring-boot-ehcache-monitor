package com.github.kingbbode.ehcache.monitor.ui.view.component;

import com.github.kingbbode.ehcache.monitor.component.CacheHistoryStore;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringView(name = "")
public class CacheInfoComponent extends CustomComponent implements View {

    private final CacheHistoryStore store;

    @Autowired
    public CacheInfoComponent(CacheManager cacheManager, CacheHistoryStore store) {
        this.store = store;
        init(cacheManager);
    }

    private void init(CacheManager cacheManager) {
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTitleBar());
        content.addComponent(createCacheGrid(cacheManager));
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(createCacheCharts(cacheManager));
        content.addComponent(panel);
        setCompositionRoot(content);
    }

    private VerticalLayout createCacheCharts(CacheManager cacheManager) {

        VerticalLayout verticalLayout = new VerticalLayout();
        List<HorizontalLayout> horizontalLayoutList = IntStream.range(0, cacheManager.getCacheNames().length)
                .mapToObj(value -> new HorizontalLayout())
                .collect(Collectors.toList());
        String[] names = cacheManager.getCacheNames();
        IntStream.range(0, names.length).forEach(value -> horizontalLayoutList.get(value / 3).addComponent(new CacheLineChart(names[value], this.store.get(names[value]))));
        horizontalLayoutList.forEach(components -> {
            verticalLayout.addComponent(components);
            components.setSizeFull();
        });
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private HorizontalLayout createTitleBar() {
        HorizontalLayout titleBar = new HorizontalLayout();
        Label title = new Label("EHCACHE LIST");
        titleBar.addComponent(title);
        titleBar.setExpandRatio(title, 1.0f);
        title.addStyleNames(ValoTheme.LABEL_H1, ValoTheme.LABEL_BOLD, ValoTheme.LABEL_COLORED);

        return titleBar;
    }

    private Grid<Cache> createCacheGrid(CacheManager cacheManager) {
        Grid<Cache> grid = new Grid<>();
        grid.addColumn(Cache::getName).setCaption("Name");
        grid.addColumn(cache -> ((Double) (((double) cache.getStatistics().cacheHitCount()) / ((double) (cache.getStatistics().cacheMissCount() + cache.getStatistics().cacheHitCount())) * 100)).intValue() + "%").setCaption("Hit Ratio");
        grid.addColumn(cache -> cache.getCacheConfiguration().getMaxEntriesLocalHeap()).setCaption("Max Size");
        grid.addColumn(Cache::getSize).setCaption("Size");
        grid.addColumn(Cache::getStatus).setCaption("Status");
        grid.addColumn(cache -> cache.getCacheConfiguration().getTimeToIdleSeconds()).setCaption("TTldle(s)");
        grid.addColumn(cache -> cache.getCacheConfiguration().getTimeToLiveSeconds()).setCaption("TTLive(s)");
        grid.addColumn(cache -> cache.getStatistics().cacheHitCount()).setCaption("hit");
        grid.addColumn(cache -> cache.getStatistics().cacheMissExpiredCount()).setCaption("miss : Expire");
        grid.addColumn(cache -> cache.getStatistics().cacheMissNotFoundCount()).setCaption("miss : Not Found");

        grid.setItems(Arrays.stream(cacheManager.getCacheNames())
                .map(cacheManager::getCache)
                .collect(Collectors.toList()));
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        int cacheSize = cacheManager.getCacheNames().length;
        if (cacheSize != 0) {
            grid.setHeightByRows(cacheSize > 10 ? 10 : cacheSize);
        }
        return grid;
    }
}
