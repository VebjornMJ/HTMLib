package dev.htmlib.api.component;

import dev.htmlib.api.HTMLibRuntime;
import org.bukkit.entity.Player;

public final class Menu extends Container<Menu> {

    public enum RenderType { DIALOG, INVENTORY }

    private final String menuId;
    private String title = "";
    private boolean canCloseWithEscape = true;
    private String submitAction;
    private String submitLabel = "Submit";
    private RenderType renderType = RenderType.DIALOG;
    private Integer inventoryRows;

    private Menu(String menuId) {
        this.menuId = menuId;
    }

    public static Menu create(String menuId) {
        return new Menu(menuId);
    }

    @Override
    public String tagName() {
        return "menu";
    }

    public String menuId() {
        return menuId;
    }

    public String title() {
        return title;
    }

    public Menu title(String title) {
        this.title = title == null ? "" : title;
        return this;
    }

    public boolean canCloseWithEscape() {
        return canCloseWithEscape;
    }

    public Menu canCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
        return this;
    }

    public String submitAction() {
        return submitAction;
    }

    public Menu submitAction(String action) {
        this.submitAction = action;
        return this;
    }

    public String submitLabel() {
        return submitLabel;
    }

    public Menu submitLabel(String label) {
        this.submitLabel = label == null ? "Submit" : label;
        return this;
    }

    public RenderType renderType() {
        return renderType;
    }

    public Menu renderType(RenderType renderType) {
        this.renderType = renderType == null ? RenderType.DIALOG : renderType;
        return this;
    }

    public Menu inventory() {
        return renderType(RenderType.INVENTORY);
    }

    public Menu dialog() {
        return renderType(RenderType.DIALOG);
    }

    public Integer inventoryRows() {
        return inventoryRows;
    }

    public Menu inventoryRows(int rows) {
        this.inventoryRows = Math.max(1, Math.min(6, rows));
        this.renderType = RenderType.INVENTORY;
        return this;
    }

    public void show(Player player) {
        HTMLibRuntime.controller().open(player, this);
    }
}
