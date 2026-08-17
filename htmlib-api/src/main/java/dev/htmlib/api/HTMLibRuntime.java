package dev.htmlib.api;

import dev.htmlib.api.navigation.MenuController;

public final class HTMLibRuntime {

    private static volatile MenuController controller;

    private HTMLibRuntime() {
    }

    public static void bind(MenuController controller) {
        HTMLibRuntime.controller = controller;
    }

    public static void unbind() {
        controller = null;
    }

    public static boolean isBound() {
        return controller != null;
    }

    public static MenuController controller() {
        MenuController c = controller;
        if (c == null) {
            throw new IllegalStateException(
                "HTMLib runtime is not initialized yet - is the HTMLib (htmlib-paper) plugin loaded and enabled?");
        }
        return c;
    }
}
