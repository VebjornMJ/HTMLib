package dev.htmlib.api.component;

public final class Image extends Component<Image> {

    private String source;
    private String fallbackIcon = "minecraft:painting";

    public Image() {
    }

    public Image(String source) {
        this.source = source;
    }

    @Override
    public String tagName() {
        return "image";
    }

    public String source() {
        return source;
    }

    public Image source(String source) {
        this.source = source;
        return this;
    }

    public String fallbackIcon() {
        return fallbackIcon;
    }

    public Image fallbackIcon(String fallbackIcon) {
        this.fallbackIcon = fallbackIcon;
        return this;
    }
}
