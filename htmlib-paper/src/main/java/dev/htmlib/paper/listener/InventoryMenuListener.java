package dev.htmlib.paper.listener;

import dev.htmlib.paper.render.InventoryRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public final class InventoryMenuListener implements Listener {

    private final InventoryRenderer renderer;

    public InventoryMenuListener(InventoryRenderer renderer) {
        this.renderer = renderer;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        if (!renderer.owns(topHolder)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getClickedInventory() == null || event.getClickedInventory().getHolder() != topHolder) {
            return;
        }
        renderer.handleSlotClick(player, topHolder, event.getSlot());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        if (renderer.owns(topHolder)) {
            event.setCancelled(true);
        }
    }
}
