package dev.htmlib.api.component;

import java.util.ArrayList;
import java.util.List;

public final class Input extends Component<Input> {

    public enum Type { TEXT, BOOLEAN, NUMBER, OPTION }

    private final String key;
    private Type type = Type.TEXT;
    private String label = "";
    private String initialText = "";
    private boolean initialBoolean = false;
    private float min = 0f;
    private float max = 100f;
    private float step = 1f;
    private float initialNumber = 0f;
    private final List<String> options = new ArrayList<>();
    private int width = 200;

    public Input(String key) {
        this.key = key;
    }

    @Override
    public String tagName() {
        return "input";
    }

    public String key() {
        return key;
    }

    public Type type() {
        return type;
    }

    public Input type(Type type) {
        this.type = type;
        return this;
    }

    public String label() {
        return label;
    }

    public Input label(String label) {
        this.label = label == null ? "" : label;
        return this;
    }

    public String initialText() {
        return initialText;
    }

    public Input initialText(String initialText) {
        this.initialText = initialText == null ? "" : initialText;
        this.type = Type.TEXT;
        return this;
    }

    public boolean initialBoolean() {
        return initialBoolean;
    }

    public Input initialBoolean(boolean initialBoolean) {
        this.initialBoolean = initialBoolean;
        this.type = Type.BOOLEAN;
        return this;
    }

    public float min() {
        return min;
    }

    public float max() {
        return max;
    }

    public float step() {
        return step;
    }

    public float initialNumber() {
        return initialNumber;
    }

    public Input numberRange(float min, float max, float step, float initial) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.initialNumber = initial;
        this.type = Type.NUMBER;
        return this;
    }

    public List<String> options() {
        return options;
    }

    public Input options(String... options) {
        this.options.clear();
        for (String o : options) {
            this.options.add(o);
        }
        this.type = Type.OPTION;
        return this;
    }

    public int width() {
        return width;
    }

    public Input width(int width) {
        this.width = width;
        return this;
    }
}
