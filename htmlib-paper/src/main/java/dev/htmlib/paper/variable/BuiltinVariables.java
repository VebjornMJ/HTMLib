package dev.htmlib.paper.variable;

import dev.htmlib.api.variable.VariableRegistry;
import org.bukkit.Bukkit;

public final class BuiltinVariables {

    private BuiltinVariables() {
    }

    public static void registerAll(VariableRegistry variables) {
        variables.register("player.name", player -> player.getName());
        variables.register("player.uuid", player -> player.getUniqueId().toString());
        variables.register("player.level", player -> Integer.toString(player.getLevel()));
        variables.register("player.health", player -> String.format("%.1f", player.getHealth()));
        variables.register("player.food", player -> Integer.toString(player.getFoodLevel()));
        variables.register("player.world", player -> player.getWorld().getName());
        variables.register("player.gamemode", player -> player.getGameMode().name());
        variables.register("player.x", player -> Integer.toString(player.getLocation().getBlockX()));
        variables.register("player.y", player -> Integer.toString(player.getLocation().getBlockY()));
        variables.register("player.z", player -> Integer.toString(player.getLocation().getBlockZ()));
        variables.register("player.ping", player -> Integer.toString(player.getPing()));

        variables.register("server.online", player -> Integer.toString(Bukkit.getOnlinePlayers().size()));
        variables.register("server.max_players", player -> Integer.toString(Bukkit.getMaxPlayers()));
        variables.register("server.name", player -> Bukkit.getServer().getName());
        variables.register("server.version", player -> Bukkit.getVersion());
        variables.register("server.tps", player -> String.format("%.2f", Bukkit.getServer().getTPS()[0]));
    }
}
