package dev.htmlib.paper.render;

import dev.htmlib.api.component.Menu;
import dev.htmlib.api.navigation.MenuRenderer;
import org.bukkit.entity.Player;

public final class PlatformMenuRenderer implements MenuRenderer {

    private final DialogRenderer dialogRenderer;
    private final InventoryRenderer inventoryRenderer;

    public PlatformMenuRenderer(DialogRenderer dialogRenderer, InventoryRenderer inventoryRenderer) {
        this.dialogRenderer = dialogRenderer;
        this.inventoryRenderer = inventoryRenderer;
    }

    @Override
    public void render(Player player, Menu menu) {
        if (menu.renderType() == Menu.RenderType.INVENTORY) {
            player.closeDialog();
            inventoryRenderer.render(player, menu);
        } else {
            if (inventoryRenderer.owns(player.getOpenInventory().getTopInventory().getHolder())) {
                player.closeInventory();
            }
            dialogRenderer.render(player, menu);
        }
    }
}
