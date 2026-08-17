package dev.htmlib.paper.demo;

import dev.htmlib.api.component.Menu;
import dev.htmlib.events.ActionRegistry;
import dev.htmlib.navigation.MenuRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class DemoMenu {

    public static final String MENU_ID = "htmlib:demo";
    public static final String INVENTORY_MENU_ID = "htmlib:demo-inventory";

    private DemoMenu() {
    }

    public static void register(MenuRegistry menus, ActionRegistry actions) {
        Menu main = Menu.create(MENU_ID)
            .title("HTMLib Demo")
            .text("Welcome, {{ player.name }}!")
            .text("Online players: {{ server.online }}")
            .row(row -> row
                .button("Survival").icon("grass_block").tooltip("Go to survival").action("htmlib:demo.survival")
                .button("PvP").icon("diamond_sword").tooltip("Go to PvP").action("htmlib:demo.pvp")
                .button("Shop").icon("emerald").tooltip("Open the shop").action("htmlib:demo.shop")
            )
            .row(row -> row
                .button("Try inventory UI").icon("chest").tooltip("Same menu system, chest GUI instead")
                    .action("open:" + INVENTORY_MENU_ID)
                .button("Close").icon("barrier").action("close")
            );

        Menu inventory = Menu.create(INVENTORY_MENU_ID)
            .inventory()
            .title("HTMLib Demo (Inventory)")
            .text("Welcome, {{ player.name }}!")
            .row(row -> row
                .button("Survival").icon("grass_block").tooltip("Go to survival").action("htmlib:demo.survival")
                .button("PvP").icon("diamond_sword").tooltip("Go to PvP").action("htmlib:demo.pvp")
                .button("Shop").icon("emerald").tooltip("Open the shop").action("htmlib:demo.shop")
            )
            .row(row -> row
                .button("Back to dialog UI").icon("writable_book").action("open:" + MENU_ID)
                .button("Close").icon("barrier").action("close")
            );

        menus.register(main);
        menus.register(inventory);

        actions.register("htmlib:demo.survival", event -> event.player().sendMessage(
            Component.text("(demo) Teleporting to survival - wire dev.htmlib.paper.demo.DemoMenu up to your server.",
                NamedTextColor.GREEN)));
        actions.register("htmlib:demo.pvp", event -> event.player().sendMessage(
            Component.text("(demo) Teleporting to PvP - wire this action up to your server.", NamedTextColor.GREEN)));
        actions.register("htmlib:demo.shop", event -> event.player().sendMessage(
            Component.text("(demo) Opening shop - wire this action up to your economy plugin.", NamedTextColor.GREEN)));
    }
}
