package dev.htmlib.layout;

import dev.htmlib.api.component.Button;
import dev.htmlib.api.component.Component;
import dev.htmlib.api.component.Container;
import dev.htmlib.api.component.Icon;
import dev.htmlib.api.component.Image;
import dev.htmlib.api.component.Input;
import dev.htmlib.api.component.ListComponent;
import dev.htmlib.api.component.Menu;
import dev.htmlib.api.component.Row;
import dev.htmlib.api.component.Text;
import dev.htmlib.api.condition.ConditionContext;

import java.util.ArrayList;
import java.util.List;

public final class LayoutEngine {

    public FlattenedMenu flatten(Menu menu, ConditionContext ctx) {
        List<Component<?>> body = new ArrayList<>();
        List<Button> buttons = new ArrayList<>();
        List<Input> inputs = new ArrayList<>();
        int[] maxColumns = {1};

        walk(menu, ctx, body, buttons, inputs, maxColumns);

        return new FlattenedMenu(body, buttons, inputs, Math.max(1, maxColumns[0]));
    }

    private void walk(Component<?> node, ConditionContext ctx, List<Component<?>> body,
                       List<Button> buttons, List<Input> inputs, int[] maxColumns) {
        if (!node.visibleIf().test(ctx)) {
            return;
        }

        if (node instanceof Button button) {
            buttons.add(button);
        } else if (node instanceof Input input) {
            inputs.add(input);
        } else if (node instanceof Text || node instanceof Icon || node instanceof Image) {
            body.add(node);
        } else if (node instanceof ListComponent<?> list) {
            expandList(list, ctx, body, buttons, inputs, maxColumns);
            for (Component<?> child : list.children()) {
                walk(child, ctx, body, buttons, inputs, maxColumns);
            }
        } else if (node instanceof Container<?> container) {
            if (isRowLike(container)) {
                int count = 0;
                for (Component<?> child : container.children()) {
                    if (child instanceof Button b && b.visibleIf().test(ctx)) {
                        count++;
                    }
                }
                if (count > maxColumns[0]) {
                    maxColumns[0] = count;
                }
            }
            for (Component<?> child : container.children()) {
                walk(child, ctx, body, buttons, inputs, maxColumns);
            }
        }
    }

    private <T> void expandList(ListComponent<T> list, ConditionContext ctx, List<Component<?>> body,
                                 List<Button> buttons, List<Input> inputs, int[] maxColumns) {
        if (!list.isDynamic()) {
            return;
        }
        List<T> items = list.dataSource().apply(ctx.player());
        for (T item : items) {
            Component<?> rendered = list.itemRenderer().apply(item);
            if (rendered != null) {
                walk(rendered, ctx, body, buttons, inputs, maxColumns);
            }
        }
    }

    private boolean isRowLike(Container<?> container) {
        if (container instanceof Row) {
            return true;
        }
        if ("horizontal".equalsIgnoreCase(container.styleOrDefault("direction", ""))) {
            return true;
        }
        return container.hasClass("row");
    }
}
