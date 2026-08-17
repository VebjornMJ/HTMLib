package dev.htmlib.paper.listener;

import dev.htmlib.navigation.NavigationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerQuitListener implements Listener {

    private final NavigationManager navigation;

    public PlayerQuitListener(NavigationManager navigation) {
        this.navigation = navigation;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        navigation.forget(event.getPlayer());
    }
}
