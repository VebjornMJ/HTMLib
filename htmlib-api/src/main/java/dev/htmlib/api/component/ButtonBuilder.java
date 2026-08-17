package dev.htmlib.api.component;

import dev.htmlib.api.condition.Condition;

public final class ButtonBuilder<P extends Container<P>> {

    private final P parent;
    private final Button button;

    public ButtonBuilder(P parent, Button button) {
        this.parent = parent;
        this.button = button;
    }

    public ButtonBuilder<P> icon(String icon) {
        button.icon(icon);
        return this;
    }

    public ButtonBuilder<P> tooltip(String tooltip) {
        button.tooltip(tooltip);
        return this;
    }

    public ButtonBuilder<P> action(String action) {
        button.action(action);
        return this;
    }

    public ButtonBuilder<P> width(int width) {
        button.width(width);
        return this;
    }

    public ButtonBuilder<P> visibleIf(Condition condition) {
        button.visibleIf(condition);
        return this;
    }

    public ButtonBuilder<P> enabledIf(Condition condition) {
        button.enabledIf(condition);
        return this;
    }

    public ButtonBuilder<P> permission(String permission) {
        button.permission(permission);
        return this;
    }

    public ButtonBuilder<P> id(String id) {
        button.id(id);
        return this;
    }

    public ButtonBuilder<P> button(String text) {
        return parent.button(text);
    }

    public P and() {
        return parent;
    }

    public Button build() {
        return button;
    }
}
