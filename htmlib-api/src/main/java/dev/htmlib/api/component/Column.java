package dev.htmlib.api.component;

public final class Column extends Container<Column> {

    public Column() {
        style("display", "flex");
        style("direction", "vertical");
        style("gap", "1");
    }

    @Override
    public String tagName() {
        return "column";
    }

    public Column gap(int gap) {
        style("gap", Integer.toString(gap));
        return this;
    }

    public Column align(String align) {
        style("align", align);
        return this;
    }
}
