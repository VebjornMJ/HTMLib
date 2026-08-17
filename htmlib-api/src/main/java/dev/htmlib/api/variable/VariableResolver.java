package dev.htmlib.api.variable;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface VariableResolver {

    String resolve(Player player);
}
