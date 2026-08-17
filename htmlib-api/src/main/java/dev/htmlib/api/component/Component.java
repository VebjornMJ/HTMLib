package dev.htmlib.api.component;

import dev.htmlib.api.condition.Condition;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class Component<SELF extends Component<SELF>> {

    private String id;
    private final Set<String> cssClasses = new LinkedHashSet<>();
    private final Map<String, String> style = new LinkedHashMap<>();
    private Condition visibleIf = Condition.always();
    private String permission;

    @SuppressWarnings("unchecked")
    protected final SELF self() {
        return (SELF) this;
    }

    public String id() {
        return id;
    }

    public SELF id(String id) {
        this.id = id;
        return self();
    }

    public Set<String> cssClasses() {
        return cssClasses;
    }

    public SELF cssClass(String... classes) {
        for (String c : classes) {
            if (c != null && !c.isBlank()) {
                cssClasses.add(c.trim());
            }
        }
        return self();
    }

    public boolean hasClass(String name) {
        return cssClasses.contains(name);
    }

    public Map<String, String> style() {
        return style;
    }

    public SELF style(String key, String value) {
        style.put(key, value);
        return self();
    }

    public String styleOrDefault(String key, String defaultValue) {
        return style.getOrDefault(key, defaultValue);
    }

    public Condition visibleIf() {
        return visibleIf;
    }

    public SELF visibleIf(Condition condition) {
        this.visibleIf = condition == null ? Condition.always() : condition;
        return self();
    }

    public SELF permission(String permission) {
        this.permission = permission;
        return visibleIf(Condition.permission(permission));
    }

    public String permissionNode() {
        return permission;
    }

    public abstract String tagName();

    @Override
    public String toString() {
        return "<" + tagName() + (id != null ? " id=" + id : "") + ">";
    }
}
