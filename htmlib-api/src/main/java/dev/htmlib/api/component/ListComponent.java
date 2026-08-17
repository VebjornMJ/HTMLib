package dev.htmlib.api.component;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class ListComponent<T> extends Container<ListComponent<T>> {

    private Function<Player, List<T>> dataSource = player -> Collections.emptyList();
    private Function<T, Component<?>> itemRenderer;

    @Override
    public String tagName() {
        return "list";
    }

    public Function<Player, List<T>> dataSource() {
        return dataSource;
    }

    public ListComponent<T> dataSource(Function<Player, List<T>> dataSource) {
        this.dataSource = dataSource == null ? player -> Collections.emptyList() : dataSource;
        return this;
    }

    public Function<T, Component<?>> itemRenderer() {
        return itemRenderer;
    }

    public ListComponent<T> itemRenderer(Function<T, Component<?>> itemRenderer) {
        this.itemRenderer = itemRenderer;
        return this;
    }

    public boolean isDynamic() {
        return itemRenderer != null;
    }
}
