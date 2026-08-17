package dev.htmlib.paper.command;

import dev.htmlib.api.component.Menu;
import dev.htmlib.paper.HTMLibPlugin;
import dev.htmlib.paper.demo.DemoMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class HTMLibCommand implements CommandExecutor, TabCompleter {

    private final HTMLibPlugin plugin;

    public HTMLibCommand(HTMLibPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                              String @NotNull [] args) {
        if (!sender.hasPermission("htmlib.admin")) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> {
                sender.sendMessage(Component.text("Registered menus:", NamedTextColor.GOLD));
                for (Menu menu : plugin.menus().all()) {
                    sender.sendMessage(Component.text(" - " + menu.menuId()));
                }
            }
            case "open" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Usage: /htmlib open <menu> [player]", NamedTextColor.RED));
                    return true;
                }
                Player target = args.length >= 3 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player p ? p : null);
                if (target == null) {
                    sender.sendMessage(Component.text(
                        "Player not found (console must specify a player name).", NamedTextColor.RED));
                    return true;
                }
                if (!plugin.menus().has(args[1])) {
                    sender.sendMessage(Component.text("No such menu: " + args[1], NamedTextColor.RED));
                    return true;
                }
                plugin.navigation().openById(target, args[1]);
                sender.sendMessage(Component.text(
                    "Opened '" + args[1] + "' for " + target.getName() + ".", NamedTextColor.GREEN));
            }
            case "demo" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Component.text("Only players can run this.", NamedTextColor.RED));
                    return true;
                }
                plugin.navigation().openById(player, DemoMenu.MENU_ID);
            }
            default -> sendUsage(sender);
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("HTMLib commands:", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/htmlib list", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/htmlib open <menu> [player]", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/htmlib demo", NamedTextColor.GRAY));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                  @NotNull String alias, String @NotNull [] args) {
        if (args.length == 1) {
            return filter(List.of("list", "open", "demo"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
            return filter(plugin.menus().all().stream().map(Menu::menuId).toList(), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("open")) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[2]);
        }
        return List.of();
    }

    private List<String> filter(List<String> options, String prefix) {
        String lower = prefix.toLowerCase();
        return options.stream().filter(o -> o.toLowerCase().startsWith(lower)).toList();
    }
}
