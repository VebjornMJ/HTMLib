package dev.htmlib.paper.render;

import dev.htmlib.api.component.Button;
import dev.htmlib.api.component.Icon;
import dev.htmlib.api.component.Image;
import dev.htmlib.api.component.Menu;
import dev.htmlib.api.component.Text;
import dev.htmlib.api.condition.ConditionContext;
import dev.htmlib.api.navigation.MenuController;
import dev.htmlib.api.navigation.MenuRenderer;
import dev.htmlib.api.variable.VariableRegistry;
import dev.htmlib.events.ActionEvent;
import dev.htmlib.events.ActionInvokedEvent;
import dev.htmlib.events.ActionRegistry;
import dev.htmlib.events.EventBus;
import dev.htmlib.layout.FlattenedMenu;
import dev.htmlib.layout.LayoutEngine;
import dev.htmlib.paper.placeholder.PlaceholderApiAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class InventoryRenderer implements MenuRenderer {

    private static final Component DENIED_MESSAGE =
        Component.text("You can't do that right now.", NamedTextColor.RED);

    private final VariableRegistry variables;
    private final LayoutEngine layout;
    private final ActionRegistry actions;
    private final EventBus events;
    private final Logger logger;
    private final Set<String> warnedMenus = ConcurrentHashMap.newKeySet();
    private MenuController controller;

    public InventoryRenderer(VariableRegistry variables, LayoutEngine layout, ActionRegistry actions,
                              EventBus events, Logger logger) {
        this.variables = variables;
        this.layout = layout;
        this.actions = actions;
        this.events = events;
        this.logger = logger;
    }

    public void bindController(MenuController controller) {
        this.controller = controller;
    }

    @Override
    public void render(Player player, Menu menu) {
        renderPage(player, menu, 0);
    }

    void renderPage(Player player, Menu menu, int requestedPage) {
        ConditionContext ctx = new ConditionContext(player);
        FlattenedMenu flattened = layout.flatten(menu, ctx);

        List<Button> buttons = new ArrayList<>(flattened.buttons());
        if (menu.submitAction() != null) {
            buttons.add(new Button().text(menu.submitLabel()).action(menu.submitAction()));
        }
        if (!flattened.inputs().isEmpty()) {
            warnInputsUnsupported(menu);
        }

        List<Entry> entries = buildEntries(flattened.body(), buttons, player);

        Integer explicitRows = menu.inventoryRows();
        int maxSize = explicitRows != null ? explicitRows * 9 : 54;
        boolean paginated = entries.size() > maxSize;

        int size;
        int pageCapacity;
        if (paginated) {
            size = Math.max(maxSize, 18);
            pageCapacity = size - 9;
        } else {
            size = Math.min(54, roundUpToNine(Math.max(entries.size(), 1)));
            pageCapacity = size;
        }

        int totalPages = paginated ? (int) Math.ceil(entries.size() / (double) pageCapacity) : 1;
        int page = Math.max(0, Math.min(requestedPage, totalPages - 1));
        int start = page * pageCapacity;
        int end = Math.min(entries.size(), start + pageCapacity);

        ItemStack[] contents = new ItemStack[size];
        Map<Integer, Button> slotButtons = new LinkedHashMap<>();
        int slot = 0;
        for (int i = start; i < end; i++) {
            Entry entry = entries.get(i);
            contents[slot] = entry.item();
            if (entry.button() != null) {
                slotButtons.put(slot, entry.button());
            }
            slot++;
        }

        Integer previousSlot = null;
        Integer nextSlot = null;
        if (paginated) {
            int controlRowStart = size - 9;
            if (page > 0) {
                previousSlot = controlRowStart;
                contents[previousSlot] = navItem("Previous Page");
            }
            contents[controlRowStart + 4] = pageInfoItem(page, totalPages);
            if (page + 1 < totalPages) {
                nextSlot = controlRowStart + 8;
                contents[nextSlot] = navItem("Next Page");
            }
        }

        Component title = text(menu.title(), player);
        if (paginated) {
            title = title.append(Component.text(" (" + (page + 1) + "/" + totalPages + ")", NamedTextColor.DARK_GRAY));
        }

        HTMLibInventoryHolder holder = new HTMLibInventoryHolder(menu, slotButtons, page, totalPages, previousSlot, nextSlot);
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        inventory.setContents(contents);
        holder.attach(inventory);
        player.openInventory(inventory);
    }

    public void handleSlotClick(Player player, Object rawHolder, int slot) {
        if (!(rawHolder instanceof HTMLibInventoryHolder holder)) {
            return;
        }
        if (holder.isPreviousSlot(slot)) {
            renderPage(player, holder.menu(), holder.page() - 1);
            return;
        }
        if (holder.isNextSlot(slot)) {
            renderPage(player, holder.menu(), holder.page() + 1);
            return;
        }
        Button button = holder.buttonAt(slot);
        if (button == null) {
            return;
        }
        handleClick(button, holder.menu(), player);
    }

    public boolean owns(Object rawHolder) {
        return rawHolder instanceof HTMLibInventoryHolder;
    }

    private void handleClick(Button button, Menu menu, Player player) {
        try {
            ConditionContext ctx = new ConditionContext(player);
            if (!button.enabledIf().test(ctx)) {
                player.sendMessage(DENIED_MESSAGE);
                return;
            }
            ActionEvent event = new ActionEvent(player, button.action(), menu, button, Map.of(), controller);
            boolean handled = actions.dispatch(event);
            events.publish(new ActionInvokedEvent(event, handled));
        } catch (Exception e) {
            logger.warning("HTMLib: action '" + button.action() + "' threw an exception: " + e);
        }
    }

    private List<Entry> buildEntries(List<dev.htmlib.api.component.Component<?>> body, List<Button> buttons, Player player) {
        List<Entry> entries = new ArrayList<>();
        for (dev.htmlib.api.component.Component<?> node : body) {
            if (node instanceof Text text) {
                entries.add(new Entry(labelItem(text.content(), player), null));
            } else if (node instanceof Icon icon) {
                entries.add(new Entry(plainItem(icon.material(), icon.count()), null));
            } else if (node instanceof Image image) {
                entries.add(new Entry(plainItem(image.fallbackIcon(), 1), null));
            }
        }
        for (Button button : buttons) {
            entries.add(new Entry(buttonItem(button, player), button));
        }
        return entries;
    }

    private ItemStack labelItem(String content, Player player) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(text(content, player).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack plainItem(String material, int count) {
        return new ItemStack(MaterialResolver.resolve(material), Math.max(1, count));
    }

    private ItemStack buttonItem(Button button, Player player) {
        ItemStack stack = new ItemStack(MaterialResolver.resolve(button.icon()), 1);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(text(button.text(), player).decoration(TextDecoration.ITALIC, false));
        if (button.tooltip() != null && !button.tooltip().isBlank()) {
            meta.lore(List.of(text(button.tooltip(), player).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack navItem(String label) {
        ItemStack stack = new ItemStack(Material.ARROW);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(label, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack pageInfoItem(int page, int totalPages) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text("Page " + (page + 1) + " / " + totalPages, NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        stack.setItemMeta(meta);
        return stack;
    }

    private void warnInputsUnsupported(Menu menu) {
        if (warnedMenus.add(menu.menuId())) {
            logger.warning("HTMLib: menu '" + menu.menuId() + "' uses type=\"inventory\" but contains <input> "
                + "fields - inventory-type menus don't support input widgets (dialog only); they will be ignored.");
        }
    }

    private int roundUpToNine(int n) {
        return Math.max(9, ((n + 8) / 9) * 9);
    }

    private Component text(String template, Player player) {
        String interpolated = variables.interpolate(template, player);
        return Component.text(PlaceholderApiAdapter.apply(interpolated, player));
    }

    private record Entry(ItemStack item, Button button) {
    }
}
