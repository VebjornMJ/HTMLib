package dev.htmlib.navigation;

import dev.htmlib.api.component.Menu;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MenuRegistry {

    private final Map<String, Menu> menus = new ConcurrentHashMap<>();

    public void register(Menu menu) {
        menus.put(menu.menuId(), menu);
    }

    public void unregister(String menuId) {
        menus.remove(menuId);
    }

    public Menu get(String menuId) {
        return menus.get(menuId);
    }

    public boolean has(String menuId) {
        return menus.containsKey(menuId);
    }

    public Collection<Menu> all() {
        return menus.values();
    }
}
