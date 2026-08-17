package dev.htmlib.api.navigation;

import dev.htmlib.api.component.Menu;
import org.bukkit.entity.Player;

public interface MenuController {

    void open(Player player, Menu menu);

    boolean openById(Player player, String menuId);

    void back(Player player);

    void close(Player player);

    Menu current(Player player);
}
