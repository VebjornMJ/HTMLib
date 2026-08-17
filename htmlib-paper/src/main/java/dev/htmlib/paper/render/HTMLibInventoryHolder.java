package dev.htmlib.paper.render;

import dev.htmlib.api.component.Button;
import dev.htmlib.api.component.Menu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

final class HTMLibInventoryHolder implements InventoryHolder {

    private final Menu menu;
    private final Map<Integer, Button> slotButtons;
    private final int page;
    private final int totalPages;
    private final Integer previousSlot;
    private final Integer nextSlot;
    private Inventory inventory;

    HTMLibInventoryHolder(Menu menu, Map<Integer, Button> slotButtons, int page, int totalPages,
                           Integer previousSlot, Integer nextSlot) {
        this.menu = menu;
        this.slotButtons = slotButtons;
        this.page = page;
        this.totalPages = totalPages;
        this.previousSlot = previousSlot;
        this.nextSlot = nextSlot;
    }

    void attach(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    Menu menu() {
        return menu;
    }

    Button buttonAt(int slot) {
        return slotButtons.get(slot);
    }

    int page() {
        return page;
    }

    int totalPages() {
        return totalPages;
    }

    boolean isPreviousSlot(int slot) {
        return previousSlot != null && previousSlot == slot;
    }

    boolean isNextSlot(int slot) {
        return nextSlot != null && nextSlot == slot;
    }
}
