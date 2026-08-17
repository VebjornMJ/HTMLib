package dev.htmlib.events;

import dev.htmlib.api.component.Menu;
import org.bukkit.entity.Player;

public record MenuCloseEvent(Player player, Menu menu) {
}
