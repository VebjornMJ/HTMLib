package dev.htmlib.api.navigation;

import dev.htmlib.api.component.Menu;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface MenuRenderer {

    void render(Player player, Menu menu);
}
