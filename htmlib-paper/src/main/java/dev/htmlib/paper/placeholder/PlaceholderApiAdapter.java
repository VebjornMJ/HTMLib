package dev.htmlib.paper.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlaceholderApiAdapter {

    private static final Pattern PLACEHOLDER = Pattern.compile("%([^%\\s]+)%");

    private PlaceholderApiAdapter() {
    }

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static String apply(String text, Player player) {
        if (text == null || text.isEmpty() || !text.contains("%") || !isAvailable()) {
            return text;
        }
        try {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        } catch (Throwable t) {
            return text;
        }
    }

    public static boolean looksLikePlaceholder(String text) {
        return text != null && PLACEHOLDER.matcher(text).find();
    }
}
