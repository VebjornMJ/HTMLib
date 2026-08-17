package dev.htmlib.api.condition;

@FunctionalInterface
public interface Condition {

    boolean test(ConditionContext context);

    default Condition and(Condition other) {
        return ctx -> this.test(ctx) && other.test(ctx);
    }

    default Condition or(Condition other) {
        return ctx -> this.test(ctx) || other.test(ctx);
    }

    default Condition negate() {
        return ctx -> !this.test(ctx);
    }

    static Condition always() {
        return ctx -> true;
    }

    static Condition never() {
        return ctx -> false;
    }

    static Condition not(Condition condition) {
        return condition.negate();
    }

    static Condition permission(String node) {
        if (node == null || node.isBlank()) {
            return always();
        }
        return ctx -> ctx.player() != null && ctx.player().hasPermission(node);
    }
}
