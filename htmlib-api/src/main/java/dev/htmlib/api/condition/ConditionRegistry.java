package dev.htmlib.api.condition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ConditionRegistry {

    private final Map<String, Function<String, Condition>> factories = new ConcurrentHashMap<>();

    public ConditionRegistry() {
        register("permission", Condition::permission);
        register("perm", Condition::permission);
    }

    public void register(String prefix, Function<String, Condition> factory) {
        factories.put(prefix.toLowerCase(), factory);
    }

    public boolean has(String prefix) {
        return factories.containsKey(prefix.toLowerCase());
    }

    public Condition parse(String expression) {
        if (expression == null || expression.isBlank()) {
            return Condition.always();
        }

        String expr = expression.trim();
        boolean negate = expr.startsWith("!");
        if (negate) {
            expr = expr.substring(1).trim();
        }

        int colon = expr.indexOf(':');
        Condition condition;
        if (colon > 0) {
            String prefix = expr.substring(0, colon).trim().toLowerCase();
            String remainder = expr.substring(colon + 1).trim();
            Function<String, Condition> factory = factories.get(prefix);
            condition = factory != null ? factory.apply(remainder) : Condition.permission(expr);
        } else {
            condition = Condition.permission(expr);
        }

        return negate ? condition.negate() : condition;
    }
}
