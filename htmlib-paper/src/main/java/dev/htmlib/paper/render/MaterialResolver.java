package dev.htmlib.paper.render;

import org.bukkit.Material;

import java.util.Locale;

final class MaterialResolver {

    private MaterialResolver() {
    }

    static Material resolve(String material) {
        if (material == null) {
            return Material.STONE;
        }
        String key = material.replace("minecraft:", "").toUpperCase(Locale.ROOT);
        Material resolved = Material.matchMaterial(key.toLowerCase(Locale.ROOT));
        if (resolved == null) {
            resolved = Material.matchMaterial(key);
        }

        return (resolved != null && resolved != Material.AIR) ? resolved : Material.STONE;
    }
}
