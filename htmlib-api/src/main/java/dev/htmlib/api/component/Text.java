package dev.htmlib.api.component;

public final class Text extends Component<Text> {

    private String content;

    public Text() {
        this("");
    }

    public Text(String content) {
        this.content = content == null ? "" : content;
    }

    @Override
    public String tagName() {
        return "text";
    }

    public String content() {
        return content;
    }

    public Text content(String content) {
        this.content = content == null ? "" : content;
        return this;
    }
}
