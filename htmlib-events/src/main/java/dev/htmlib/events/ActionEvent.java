package dev.htmlib.events;

import dev.htmlib.api.component.Button;
import dev.htmlib.api.component.Menu;
import dev.htmlib.api.navigation.MenuController;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;

public final class ActionEvent {

    private final Player player;
    private final String actionId;
    private final Menu menu;
    private final Button source;
    private final Map<String, Object> inputs;
    private final MenuController controller;

    public ActionEvent(Player player, String actionId, Menu menu, Button source,
                        Map<String, Object> inputs, MenuController controller) {
        this.player = player;
        this.actionId = actionId;
        this.menu = menu;
        this.source = source;
        this.inputs = inputs == null ? Collections.emptyMap() : inputs;
        this.controller = controller;
    }

    public Player player() {
        return player;
    }

    public String actionId() {
        return actionId;
    }

    public Menu menu() {
        return menu;
    }

    public Button source() {
        return source;
    }

    public Map<String, Object> inputs() {
        return inputs;
    }

    public String inputText(String key) {
        Object v = inputs.get(key);
        return v == null ? null : v.toString();
    }

    public Boolean inputBoolean(String key) {
        Object v = inputs.get(key);
        return v instanceof Boolean b ? b : null;
    }

    public Float inputNumber(String key) {
        Object v = inputs.get(key);
        return v instanceof Number n ? n.floatValue() : null;
    }

    public void open(String menuId) {
        controller.openById(player, menuId);
    }

    public void open(Menu menu) {
        controller.open(player, menu);
    }

    public void back() {
        controller.back(player);
    }

    public void close() {
        controller.close(player);
    }
}
