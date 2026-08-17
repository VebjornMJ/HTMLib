package dev.htmlib.paper.render;

import dev.htmlib.api.component.Button;
import dev.htmlib.api.component.Icon;
import dev.htmlib.api.component.Image;
import dev.htmlib.api.component.Input;
import dev.htmlib.api.component.Menu;
import dev.htmlib.api.component.Text;
import dev.htmlib.api.condition.ConditionContext;
import dev.htmlib.api.navigation.MenuController;
import dev.htmlib.api.navigation.MenuRenderer;
import dev.htmlib.api.variable.VariableRegistry;
import dev.htmlib.events.ActionEvent;
import dev.htmlib.events.ActionInvokedEvent;
import dev.htmlib.events.ActionRegistry;
import dev.htmlib.events.EventBus;
import dev.htmlib.layout.FlattenedMenu;
import dev.htmlib.layout.LayoutEngine;
import dev.htmlib.paper.placeholder.PlaceholderApiAdapter;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class DialogRenderer implements MenuRenderer {

    private static final int DEFAULT_BUTTON_WIDTH = 150;
    private static final int DEFAULT_ICON_WIDTH = 16;
    private static final int DEFAULT_ICON_HEIGHT = 16;
    private static final Component DENIED_MESSAGE =
        Component.text("You can't do that right now.", NamedTextColor.RED);

    private final VariableRegistry variables;
    private final LayoutEngine layout;
    private final ActionRegistry actions;
    private final EventBus events;
    private final Logger logger;
    private MenuController controller;

    public DialogRenderer(VariableRegistry variables, LayoutEngine layout, ActionRegistry actions,
                           EventBus events, Logger logger) {
        this.variables = variables;
        this.layout = layout;
        this.actions = actions;
        this.events = events;
        this.logger = logger;
    }

    public void bindController(MenuController controller) {
        this.controller = controller;
    }

    @Override
    public void render(Player player, Menu menu) {
        ConditionContext ctx = new ConditionContext(player);
        FlattenedMenu flattened = layout.flatten(menu, ctx);

        List<Button> buttons = new ArrayList<>(flattened.buttons());
        if (menu.submitAction() != null) {
            buttons.add(new Button().text(menu.submitLabel()).action(menu.submitAction()));
        }

        DialogBase base = DialogBase.create(
            text(menu.title(), player),
            null,
            menu.canCloseWithEscape(),
            false,
            DialogBase.DialogAfterAction.NONE,
            buildBody(flattened, player),
            buildInputs(flattened.inputs(), player)
        );

        List<ActionButton> actionButtons = new ArrayList<>();
        for (Button button : buttons) {
            actionButtons.add(buildActionButton(button, menu, player, flattened.inputs()));
        }

        DialogType type = actionButtons.isEmpty()
            ? DialogType.notice()
            : DialogType.multiAction(actionButtons, null, Math.max(1, flattened.columns()));

        Dialog dialog = Dialog.create(factory -> factory.empty().base(base).type(type));
        player.showDialog(dialog);
    }

    private List<DialogBody> buildBody(FlattenedMenu flattened, Player player) {
        List<DialogBody> body = new ArrayList<>();
        for (dev.htmlib.api.component.Component<?> node : flattened.body()) {
            if (node instanceof Text textNode) {
                body.add(DialogBody.plainMessage(text(textNode.content(), player)));
            } else if (node instanceof Icon icon) {
                body.add(DialogBody.item(itemStack(icon.material(), icon.count()), null,
                    true, icon.showTooltip(), DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT));
            } else if (node instanceof Image image) {
                body.add(DialogBody.item(itemStack(image.fallbackIcon(), 1), null,
                    true, true, DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT));
            }
        }
        return body;
    }

    private List<DialogInput> buildInputs(List<Input> inputs, Player player) {
        List<DialogInput> result = new ArrayList<>();
        for (Input input : inputs) {
            Component label = text(input.label(), player);
            int width = clampWidth(input.width());
            result.add(switch (input.type()) {
                case TEXT -> DialogInput.text(input.key(), width, label, true,
                    input.initialText(), 1024, null);
                case BOOLEAN -> DialogInput.bool(input.key(), label, input.initialBoolean(), "true", "false");
                case NUMBER -> DialogInput.numberRange(input.key(), width, label, "%s",
                    input.min(), input.max(), input.initialNumber(), input.step());
                case OPTION -> {
                    List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>();
                    for (String option : input.options()) {
                        entries.add(SingleOptionDialogInput.OptionEntry.create(
                            option, Component.text(option), false));
                    }
                    yield DialogInput.singleOption(input.key(), width, entries, label, true);
                }
            });
        }
        return result;
    }

    private ActionButton buildActionButton(Button button, Menu menu, Player player, List<Input> inputs) {
        Component label = text(button.text(), player);
        Component tooltip = button.tooltip() != null ? text(button.tooltip(), player) : null;
        int width = clampWidth(button.width() != null ? button.width() : DEFAULT_BUTTON_WIDTH);

        DialogAction action = DialogAction.customClick(
            (DialogResponseView view, Audience audience) -> handleClick(button, menu, player, inputs, view),
            ClickCallback.Options.builder().uses(64).build()
        );

        return ActionButton.create(label, tooltip, width, action);
    }

    private void handleClick(Button button, Menu menu, Player player, List<Input> inputs,
                              DialogResponseView view) {
        try {
            ConditionContext ctx = new ConditionContext(player);
            if (!button.enabledIf().test(ctx)) {
                player.sendMessage(DENIED_MESSAGE);
                return;
            }

            Map<String, Object> inputValues = collectInputs(inputs, view);
            ActionEvent event = new ActionEvent(player, button.action(), menu, button, inputValues, controller);
            boolean handled = actions.dispatch(event);
            events.publish(new ActionInvokedEvent(event, handled));
        } catch (Exception e) {
            logger.warning("HTMLib: action '" + button.action() + "' threw an exception: " + e);
        }
    }

    private Map<String, Object> collectInputs(List<Input> inputs, DialogResponseView view) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Input input : inputs) {
            Object value = switch (input.type()) {
                case TEXT, OPTION -> view.getText(input.key());
                case BOOLEAN -> view.getBoolean(input.key());
                case NUMBER -> view.getFloat(input.key());
            };
            if (value != null) {
                values.put(input.key(), value);
            }
        }
        return values;
    }

    private int clampWidth(int width) {
        return Math.max(1, Math.min(1024, width));
    }

    private Component text(String template, Player player) {
        String interpolated = variables.interpolate(template, player);
        return Component.text(PlaceholderApiAdapter.apply(interpolated, player));
    }

    private ItemStack itemStack(String material, int count) {
        return new ItemStack(MaterialResolver.resolve(material), Math.max(1, count));
    }
}
