package dev.htmlib.api.variable;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VariableRegistry {

    private static final Pattern TOKEN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.\\-]+)\\s*}}");

    private final Map<String, VariableResolver> resolvers = new ConcurrentHashMap<>();

    private Function<String, VariableResolver> fallback;

    public void register(String key, VariableResolver resolver) {
        resolvers.put(key, resolver);
    }

    public void unregister(String key) {
        resolvers.remove(key);
    }

    public boolean has(String key) {
        return resolvers.containsKey(key);
    }

    public void fallback(Function<String, VariableResolver> fallback) {
        this.fallback = fallback;
    }

    public String resolve(String key, Player player) {
        VariableResolver resolver = resolvers.get(key);
        if (resolver == null && fallback != null) {
            resolver = fallback.apply(key);
        }
        if (resolver == null) {
            return "{{ " + key + " }}";
        }
        try {
            String value = resolver.resolve(player);
            return value == null ? "" : value;
        } catch (Exception e) {
            return "";
        }
    }

    public String interpolate(String template, Player player) {
        if (template == null || template.isEmpty() || !template.contains("{{")) {
            return template == null ? "" : template;
        }
        Matcher matcher = TOKEN.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            matcher.appendReplacement(result, Matcher.quoteReplacement(resolve(key, player)));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
