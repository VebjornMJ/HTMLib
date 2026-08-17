package dev.htmlib.layout;

import dev.htmlib.api.component.Button;
import dev.htmlib.api.component.Component;
import dev.htmlib.api.component.Input;

import java.util.List;

public record FlattenedMenu(
    List<Component<?>> body,
    List<Button> buttons,
    List<Input> inputs,
    int columns
) {
}
