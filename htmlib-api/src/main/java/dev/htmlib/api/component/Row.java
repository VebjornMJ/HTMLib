package dev.htmlib.api.component;

public final class Row extends Container<Row> {

    public Row() {
        style("display", "flex");
        style("direction", "horizontal");
        style("gap", "1");
    }

    @Override
    public String tagName() {
        return "row";
    }

    public Row gap(int gap) {
        style("gap", Integer.toString(gap));
        return this;
    }

    public Row align(String align) {
        style("align", align);
        return this;
    }
}
