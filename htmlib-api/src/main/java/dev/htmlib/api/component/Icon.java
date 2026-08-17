package dev.htmlib.api.component;

public final class Icon extends Component<Icon> {

    private String material;
    private int count = 1;
    private boolean showTooltip = true;

    public Icon() {
    }

    public Icon(String material) {
        this.material = material;
    }

    @Override
    public String tagName() {
        return "icon";
    }

    public String material() {
        return material;
    }

    public Icon material(String material) {
        this.material = material;
        return this;
    }

    public int count() {
        return count;
    }

    public Icon count(int count) {
        this.count = Math.max(1, count);
        return this;
    }

    public boolean showTooltip() {
        return showTooltip;
    }

    public Icon showTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
        return this;
    }
}
