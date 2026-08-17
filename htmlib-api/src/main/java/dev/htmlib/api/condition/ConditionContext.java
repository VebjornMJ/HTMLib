package dev.htmlib.api.condition;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ConditionContext {

    private final Player player;
    private final Map<String, Object> attributes = new HashMap<>();

    public ConditionContext(Player player) {
        this.player = player;
    }

    public Player player() {
        return player;
    }

    public ConditionContext set(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public Object get(String key) {
        return attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }
}
