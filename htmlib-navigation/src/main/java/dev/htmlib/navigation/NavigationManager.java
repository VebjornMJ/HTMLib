package dev.htmlib.navigation;

import dev.htmlib.api.component.Menu;
import dev.htmlib.api.navigation.MenuController;
import dev.htmlib.api.navigation.MenuRenderer;
import dev.htmlib.events.EventBus;
import dev.htmlib.events.MenuBackEvent;
import dev.htmlib.events.MenuCloseEvent;
import dev.htmlib.events.MenuOpenEvent;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NavigationManager implements MenuController {

    private static final int MAX_HISTORY = 32;

    private final Map<UUID, Deque<Menu>> history = new ConcurrentHashMap<>();
    private final MenuRegistry registry;
    private final EventBus events;
    private final MenuRenderer renderer;

    public NavigationManager(MenuRegistry registry, EventBus events, MenuRenderer renderer) {
        this.registry = registry;
        this.events = events;
        this.renderer = renderer;
    }

    @Override
    public void open(Player player, Menu menu) {
        if (menu == null) {
            return;
        }
        Deque<Menu> stack = history.computeIfAbsent(player.getUniqueId(), id -> new ArrayDeque<>());
        stack.push(menu);
        while (stack.size() > MAX_HISTORY) {
            stack.removeLast();
        }
        renderer.render(player, menu);
        events.publish(new MenuOpenEvent(player, menu));
    }

    @Override
    public boolean openById(Player player, String menuId) {
        Menu menu = registry.get(menuId);
        if (menu == null) {
            return false;
        }
        open(player, menu);
        return true;
    }

    @Override
    public void back(Player player) {
        Deque<Menu> stack = history.get(player.getUniqueId());
        if (stack == null || stack.isEmpty()) {
            return;
        }
        stack.pop();
        Menu previous = stack.peek();
        if (previous == null) {
            close(player);
            return;
        }
        renderer.render(player, previous);
        events.publish(new MenuBackEvent(player, previous));
    }

    @Override
    public void close(Player player) {
        Deque<Menu> stack = history.remove(player.getUniqueId());
        Menu last = stack != null ? stack.peek() : null;
        if (last != null && last.renderType() == Menu.RenderType.INVENTORY) {
            player.closeInventory();
        } else {
            player.closeDialog();
        }
        if (last != null) {
            events.publish(new MenuCloseEvent(player, last));
        }
    }

    @Override
    public Menu current(Player player) {
        Deque<Menu> stack = history.get(player.getUniqueId());
        return stack == null ? null : stack.peek();
    }

    public void forget(Player player) {
        history.remove(player.getUniqueId());
    }

    public MenuRegistry menus() {
        return registry;
    }
}
