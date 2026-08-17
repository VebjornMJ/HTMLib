package dev.htmlib.events;

@FunctionalInterface
public interface Action {

    void handle(ActionEvent event);
}
