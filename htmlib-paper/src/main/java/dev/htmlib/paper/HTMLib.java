package dev.htmlib.paper;

import dev.htmlib.api.component.Menu;
import dev.htmlib.api.condition.ConditionRegistry;
import dev.htmlib.api.variable.VariableRegistry;
import dev.htmlib.events.Action;
import dev.htmlib.events.ActionRegistry;
import dev.htmlib.events.EventBus;
import dev.htmlib.navigation.MenuRegistry;
import org.bukkit.entity.Player;

public final class HTMLib {

    private static volatile HTMLibPlugin plugin;

    private HTMLib() {
    }

    static void init(HTMLibPlugin instance) {
        plugin = instance;
    }

    static void shutdown() {
        plugin = null;
    }

    private static HTMLibPlugin plugin() {
        HTMLibPlugin p = plugin;
        if (p == null) {
            throw new IllegalStateException("HTMLib is not enabled yet");
        }
        return p;
    }

    public static void registerAction(String id, Action action) {
        plugin().actions().register(id, action);
    }

    public static ActionRegistry actions() {
        return plugin().actions();
    }

    public static VariableRegistry variables() {
        return plugin().variables();
    }

    public static ConditionRegistry conditions() {
        return plugin().conditions();
    }

    public static MenuRegistry menus() {
        return plugin().menus();
    }

    public static EventBus events() {
        return plugin().events();
    }

    public static void open(Player player, String menuId) {
        plugin().navigation().openById(player, menuId);
    }

    public static void open(Player player, Menu menu) {
        plugin().navigation().open(player, menu);
    }

    public static void back(Player player) {
        plugin().navigation().back(player);
    }

    public static void close(Player player) {
        plugin().navigation().close(player);
    }
}
