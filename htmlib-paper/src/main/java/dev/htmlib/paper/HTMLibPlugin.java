package dev.htmlib.paper;

import dev.htmlib.api.HTMLibRuntime;
import dev.htmlib.api.condition.ConditionRegistry;
import dev.htmlib.api.variable.VariableRegistry;
import dev.htmlib.events.ActionRegistry;
import dev.htmlib.events.EventBus;
import dev.htmlib.layout.LayoutEngine;
import dev.htmlib.navigation.MenuRegistry;
import dev.htmlib.navigation.NavigationManager;
import dev.htmlib.paper.command.HTMLibCommand;
import dev.htmlib.paper.demo.DemoMenu;
import dev.htmlib.paper.listener.InventoryMenuListener;
import dev.htmlib.paper.listener.PlayerQuitListener;
import dev.htmlib.paper.placeholder.PlaceholderApiAdapter;
import dev.htmlib.paper.render.DialogRenderer;
import dev.htmlib.paper.render.InventoryRenderer;
import dev.htmlib.paper.render.PlatformMenuRenderer;
import dev.htmlib.paper.variable.BuiltinVariables;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class HTMLibPlugin extends JavaPlugin {

    private VariableRegistry variables;
    private ConditionRegistry conditions;
    private ActionRegistry actions;
    private EventBus events;
    private MenuRegistry menus;
    private NavigationManager navigation;

    @Override
    public void onEnable() {
        variables = new VariableRegistry();
        BuiltinVariables.registerAll(variables);

        conditions = new ConditionRegistry();
        actions = new ActionRegistry();
        events = new EventBus();
        menus = new MenuRegistry();
        LayoutEngine layout = new LayoutEngine();

        DialogRenderer dialogRenderer = new DialogRenderer(variables, layout, actions, events, getLogger());
        InventoryRenderer inventoryRenderer = new InventoryRenderer(variables, layout, actions, events, getLogger());
        PlatformMenuRenderer renderer = new PlatformMenuRenderer(dialogRenderer, inventoryRenderer);
        navigation = new NavigationManager(menus, events, renderer);
        dialogRenderer.bindController(navigation);
        inventoryRenderer.bindController(navigation);

        HTMLibRuntime.bind(navigation);
        HTMLib.init(this);

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(navigation), this);
        getServer().getPluginManager().registerEvents(new InventoryMenuListener(inventoryRenderer), this);

        PluginCommand command = getCommand("htmlib");
        if (command != null) {
            HTMLibCommand executor = new HTMLibCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        DemoMenu.register(menus, actions);

        if (PlaceholderApiAdapter.isAvailable()) {
            getLogger().info("PlaceholderAPI detected - %placeholder% tokens will resolve in menu text.");
        }

        getLogger().info("HTMLib enabled - menus render as Mojang dialogs or chest inventories, your choice per menu.");
    }

    @Override
    public void onDisable() {
        HTMLibRuntime.unbind();
        HTMLib.shutdown();
    }

    public VariableRegistry variables() {
        return variables;
    }

    public ConditionRegistry conditions() {
        return conditions;
    }

    public ActionRegistry actions() {
        return actions;
    }

    public EventBus events() {
        return events;
    }

    public MenuRegistry menus() {
        return menus;
    }

    public NavigationManager navigation() {
        return navigation;
    }
}
