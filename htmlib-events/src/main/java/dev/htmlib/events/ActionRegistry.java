package dev.htmlib.events;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ActionRegistry {

    private final Map<String, Action> actions = new ConcurrentHashMap<>();

    public void register(String id, Action action) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("action id must not be blank");
        }
        actions.put(id, action);
    }

    public void unregister(String id) {
        actions.remove(id);
    }

    public boolean has(String id) {
        return isNavigation(id) || actions.containsKey(id);
    }

    public boolean isNavigation(String id) {
        return id != null && (id.equals("back") || id.equals("close") || id.startsWith("open:"));
    }

    public boolean dispatch(ActionEvent event) {
        String id = event.actionId();
        if (id == null || id.isBlank()) {
            return false;
        }
        if (id.equals("back")) {
            event.back();
            return true;
        }
        if (id.equals("close")) {
            event.close();
            return true;
        }
        if (id.startsWith("open:")) {
            event.open(id.substring("open:".length()));
            return true;
        }
        Action action = actions.get(id);
        if (action == null) {
            return false;
        }
        action.handle(event);
        return true;
    }
}
