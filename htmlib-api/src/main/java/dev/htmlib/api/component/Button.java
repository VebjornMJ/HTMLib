package dev.htmlib.api.component;

import dev.htmlib.api.condition.Condition;

public final class Button extends Component<Button> {

    private String text = "";
    private String icon;
    private String tooltip;
    private String action;
    private Integer width;
    private Condition enabledIf = Condition.always();

    @Override
    public String tagName() {
        return "button";
    }

    public String text() {
        return text;
    }

    public Button text(String text) {
        this.text = text == null ? "" : text;
        return this;
    }

    public String icon() {
        return icon;
    }

    public Button icon(String icon) {
        this.icon = icon;
        return this;
    }

    public String tooltip() {
        return tooltip;
    }

    public Button tooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public String action() {
        return action;
    }

    public Button action(String action) {
        this.action = action;
        return this;
    }

    public Integer width() {
        return width;
    }

    public Button width(int width) {
        this.width = width;
        return this;
    }

    public Condition enabledIf() {
        return enabledIf;
    }

    public Button enabledIf(Condition condition) {
        this.enabledIf = condition == null ? Condition.always() : condition;
        return this;
    }
}
