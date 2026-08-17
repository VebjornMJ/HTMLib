package dev.htmlib.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventBus {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <E> void subscribe(Class<E> type, Consumer<E> listener) {
        listeners.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>())
            .add((Consumer<Object>) listener);
    }

    public void publish(Object event) {
        List<Consumer<Object>> subs = listeners.get(event.getClass());
        if (subs == null) {
            return;
        }
        for (Consumer<Object> listener : subs) {
            listener.accept(event);
        }
    }
}
