package com.github.kingbbode.ehcache.monitor.ui.view.component;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.sf.ehcache.CacheManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YG-MAC on 2017. 12. 16..
 */
public class Menu extends CssLayout {
    static final String CACHE_PARAMETER_KEY = "cache";
    private static final String VALO_MENUITEMS = "valo-menuitems";
    private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
    private final CacheManager cacheManager;
    private final Navigator navigator;
    private Map<String, Button> viewButtons = new HashMap<>();

    private CssLayout menuItemsLayout;
    private CssLayout menuPart;

    public Menu(Navigator navigator, CacheManager cacheManager) {
        this.navigator = navigator;
        this.cacheManager = cacheManager;
        initMenu();
        initCacheInfo();
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        addComponent(this.menuPart);
    }

    private void initMenu() {
        this.menuPart = new CssLayout();
        this.menuPart.addStyleName(ValoTheme.MENU_PART);
        // header of the menu
        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        Label title = new Label("EHCACHE MONITOR");
        title.addStyleNames(ValoTheme.LABEL_H3, ValoTheme.LABEL_BOLD);
        title.setSizeUndefined();
        /*Image image = new Image(null, new
        ThemeResource("img/table-logo.png"));
        image.setStyleName("logo");
        top.addComponent(image);*/
        top.addComponent(title);
        this.menuPart.addComponent(top);
        final Button showMenu = new Button("Menu", (Button.ClickListener) event -> {
            if (this.menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
                this.menuPart.removeStyleName(VALO_MENU_VISIBLE);
            } else {
                this.menuPart.addStyleName(VALO_MENU_VISIBLE);
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(VaadinIcons.MENU);
        menuPart.addComponent(showMenu);
        menuItemsLayout = new CssLayout();
        menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);
        menuPart.addComponent(menuItemsLayout);
    }


    private void initCacheInfo() {
        addViewButton("", "전체", VaadinIcons.LIST);
        Arrays.stream(this.cacheManager.getCacheNames()).forEach(cacheName -> addViewButton(cacheName, cacheName, VaadinIcons.MAILBOX));
    }

    /**
     * Creates a navigation button to the view identified by {@code name} using
     * {@code caption} and {@code icon}.
     *
     * @param name    view name
     * @param caption view caption in the menu
     * @param icon    view icon in the menu
     */
    public void addViewButton(final String name, String caption,
                              Resource icon) {
        Button button = new Button(caption, (Button.ClickListener) event -> navigator.navigateTo("detail/" + CACHE_PARAMETER_KEY + "=" + name));
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        button.setIcon(icon);


        menuItemsLayout.addComponent(button);
        viewButtons.put(name, button);
    }

    /**
     * Highlights a view navigation button as the currently active view in the
     * menu. This method does not perform the actual navigation.
     *
     * @param viewName the name of the view to show as active
     */
    public void setActiveView(String viewName) {
        for (Button button : viewButtons.values()) {
            button.removeStyleName("selected");
        }
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
        menuPart.removeStyleName(VALO_MENU_VISIBLE);
    }
}
