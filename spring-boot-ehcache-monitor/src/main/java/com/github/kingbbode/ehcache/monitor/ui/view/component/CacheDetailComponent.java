package com.github.kingbbode.ehcache.monitor.ui.view.component;

import com.github.kingbbode.ehcache.monitor.utils.DateTimeUtils;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by YG-MAC on 2017. 12. 16..
 */
public class CacheDetailComponent extends CustomComponent implements View {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEARCH_DEFAULT = "";

    private final CacheManager cacheManager;
    private Cache ehcache;
    private Grid<Cache> infoGrid;
    private Grid<Element> detailGrid;
    private TextField searchTextField;

    public CacheDetailComponent(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private void init(String name) {
        this.ehcache = this.cacheManager.getCache(name);
        this.infoGrid = createCacheInfoGrid();
        this.detailGrid = createDetailGrid();
        this.searchTextField = createSearchTextField();
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTitleBar());
        content.addComponent(createControlBar());
        content.addComponent(this.infoGrid);
        content.addComponent(this.detailGrid);
        setSizeFull();
        setCompositionRoot(content);
    }

    private TextField createSearchTextField() {
        TextField textField = new TextField();
        textField.addShortcutListener(new ShortcutListener("Enter Keyword",
                ShortcutAction.KeyCode.ENTER, new int[]{}) {
            @Override
            public void handleAction(Object sender, Object target) {
                searchAction();
            }
        });
        return textField;
    }

    private HorizontalLayout createTitleBar() {
        HorizontalLayout titleBar = new HorizontalLayout();
        Label title = new Label("EHCACHE DETAIL");
        titleBar.addComponent(title);
        titleBar.setExpandRatio(title, 1.0f); // Expand
        title.addStyleNames(ValoTheme.LABEL_H1, ValoTheme.LABEL_BOLD, ValoTheme.LABEL_COLORED);
        return titleBar;
    }

    private HorizontalLayout createControlBar() {
        HorizontalLayout controlBar = new HorizontalLayout();
        controlBar.addComponent(new Button("Refresh", (Button.ClickListener) event -> refresh()));
        controlBar.addComponent(new Button("Flush", (Button.ClickListener) event -> {
            this.ehcache.removeAll();
            refresh();
        }));
        controlBar.addComponent(createSearchBox());
        controlBar.setWidth("50%");
        return controlBar;
    }

    private HorizontalLayout createSearchBox() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        this.searchTextField.setWidth("70%");
        horizontalLayout.addComponent(this.searchTextField);
        Button button = new Button(VaadinIcons.SEARCH);
        button.addClickListener(event -> searchAction());
        button.setWidth("30%");
        horizontalLayout.addComponent(button);
        return horizontalLayout;
    }

    private Grid<Cache> createCacheInfoGrid() {
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
        grid.setItems(Collections.singletonList(this.ehcache));
        grid.setWidth("100%");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setHeightByRows(1);
        return grid;
    }

    private Grid<Element> createDetailGrid() {
        Grid<Element> grid = new Grid<>();
        grid.addColumn(Element::getObjectKey).setCaption("Name");
        grid.addColumn(Element::getObjectValue).setCaption("Value");
        grid.addColumn(element -> DateTimeUtils.ofPattern(element.getCreationTime(), FORMATTER)).setCaption("Create Time");
        grid.addColumn(element -> DateTimeUtils.ofPattern(element.getLastAccessTime(), FORMATTER)).setCaption("Access Time");
        grid.addColumn(element -> DateTimeUtils.ofPattern(element.getLastUpdateTime(), FORMATTER)).setCaption("Update Time");
        grid.addColumn(Element::getVersion).setCaption("Version");
        grid.addColumn(Element::getHitCount).setCaption("Hit");
        grid.addColumn(element -> {
            Button button = new Button(VaadinIcons.TRASH);
            button.addClickListener(event -> {
                this.ehcache.remove(element.getObjectKey());
                refresh();
            });
            return button;
        }, new ComponentRenderer()).setCaption("");
        grid.setItems(this.ehcache.getAll(getKeys(this.ehcache, SEARCH_DEFAULT)).values());
        grid.setWidth("100%");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        int ehcacheSize = this.ehcache.getKeys().size();
        if (ehcacheSize != 0) {
            grid.setHeightByRows(ehcacheSize > 15 ? 15 : ehcacheSize);
        }
        return grid;
    }

    @SuppressWarnings("unchecked")
    private List<?> getKeys(Cache ehcache, String keyword) {
        return (List<?>) ehcache.getKeys()
                .stream()
                .filter(o -> o instanceof String)
                .filter(o -> ((String) o).contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String cacheName = event.getParameterMap().getOrDefault(Menu.CACHE_PARAMETER_KEY, "");
        if ("".equals(cacheName)) {
            event.getNavigator().navigateTo(cacheName);
            return;
        }
        init(cacheName);
    }

    private void refresh() {
        refreshTextField();
        refreshInfoGrid();
        refreshDetailGrid();
    }

    private void refreshTextField() {
        this.searchTextField.setValue(SEARCH_DEFAULT);
    }

    private void refreshInfoGrid() {
        this.infoGrid.setItems(Collections.singletonList(this.ehcache));
    }

    private void refreshDetailGrid() {
        this.detailGrid.setItems(this.ehcache.getAll(getKeys(this.ehcache, SEARCH_DEFAULT)).values());
    }

    private void searchAction() {
        String value = this.searchTextField.getValue();
        if (StringUtils.isEmpty(value)) {
            return;
        }
        this.detailGrid.setItems(ehcache.getAll(getKeys(ehcache, value)).values());
    }
}
