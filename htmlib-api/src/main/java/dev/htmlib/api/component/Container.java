package dev.htmlib.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Container<SELF extends Container<SELF>> extends Component<SELF> {

    private final List<Component<?>> children = new ArrayList<>();

    public List<Component<?>> children() {
        return children;
    }

    public SELF add(Component<?>... components) {
        for (Component<?> c : components) {
            if (c != null) {
                children.add(c);
            }
        }
        return self();
    }

    public SELF add(Iterable<? extends Component<?>> components) {
        components.forEach(children::add);
        return self();
    }

    public SELF text(String content) {
        add(new Text(content));
        return self();
    }

    public ButtonBuilder<SELF> button(String text) {
        Button button = new Button().text(text);
        add(button);
        return new ButtonBuilder<>(self(), button);
    }

    public SELF row(Consumer<Row> configurer) {
        Row row = new Row();
        add(row);
        if (configurer != null) {
            configurer.accept(row);
        }
        return self();
    }

    public SELF column(Consumer<Column> configurer) {
        Column column = new Column();
        add(column);
        if (configurer != null) {
            configurer.accept(column);
        }
        return self();
    }

    public SELF div(Consumer<Div> configurer) {
        Div div = new Div();
        add(div);
        if (configurer != null) {
            configurer.accept(div);
        }
        return self();
    }
}
