package dev.htmlib.markup;

import dev.htmlib.api.component.Button;
import dev.htmlib.api.component.Column;
import dev.htmlib.api.component.Component;
import dev.htmlib.api.component.Container;
import dev.htmlib.api.component.Div;
import dev.htmlib.api.component.Icon;
import dev.htmlib.api.component.Image;
import dev.htmlib.api.component.Input;
import dev.htmlib.api.component.ListComponent;
import dev.htmlib.api.component.Menu;
import dev.htmlib.api.component.Row;
import dev.htmlib.api.component.Text;
import dev.htmlib.api.condition.ConditionRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MarkupLoader {

    private MarkupLoader() {
    }

    public static Menu parse(String markup) {
        return parse(markup, new ConditionRegistry());
    }

    public static Menu parseFile(Path file) {
        return parseFile(file, new ConditionRegistry());
    }

    public static Menu parseFile(Path file, ConditionRegistry conditions) {
        try {
            return parse(Files.readString(file), conditions);
        } catch (IOException e) {
            throw new MarkupException("failed to read markup file " + file, e);
        }
    }

    public static Menu parse(String markup, ConditionRegistry conditions) {
        Document document = parseDocument(markup);
        Element root = document.getDocumentElement();
        if (root == null || !"menu".equalsIgnoreCase(root.getTagName())) {
            throw new MarkupException("root element must be <menu>, found <"
                + (root == null ? "?" : root.getTagName()) + ">");
        }

        String id = attr(root, "id", "menu-" + Integer.toHexString(System.identityHashCode(root)));
        Menu menu = Menu.create(id);
        applyCommonAttributes(menu, root, conditions);
        menu.title(attr(root, "title", ""));
        menu.canCloseWithEscape(!"false".equalsIgnoreCase(attr(root, "close-on-escape", "true")));

        String renderType = attr(root, "type", "dialog").trim().toLowerCase();
        if ("inventory".equals(renderType)) {
            menu.inventory();
        } else if (!"dialog".equals(renderType)) {
            throw new MarkupException("<menu type=\"" + renderType + "\"> is not recognized (use \"dialog\" or \"inventory\")");
        }
        String rows = attr(root, "rows", null);
        if (rows != null) {
            menu.inventoryRows(Integer.parseInt(rows.trim()));
        }

        String submitAction = attr(root, "submit-action", null);
        if (submitAction != null) {
            menu.submitAction(submitAction);
        }
        String submitLabel = attr(root, "submit-label", null);
        if (submitLabel != null) {
            menu.submitLabel(submitLabel);
        }

        Map<String, Map<String, String>> cssRules = new LinkedHashMap<>();
        for (Element child : children(root)) {
            if ("style".equalsIgnoreCase(child.getTagName())) {
                cssRules.putAll(CssParser.parse(child.getTextContent()));
                continue;
            }
            Component<?> built = buildNode(child, conditions);
            if (built != null) {
                menu.add(built);
            }
        }

        if (!cssRules.isEmpty()) {
            applyCss(menu, cssRules);
        }

        return menu;
    }

    private static Component<?> buildNode(Element el, ConditionRegistry conditions) {
        String tag = el.getTagName().toLowerCase();
        return switch (tag) {
            case "div" -> buildContainer(new Div(), el, conditions);
            case "row" -> buildContainer(new Row(), el, conditions);
            case "column" -> buildContainer(new Column(), el, conditions);
            case "list" -> buildContainer(new ListComponent<>(), el, conditions);
            case "button" -> buildButton(el, conditions);
            case "text" -> buildText(el, conditions);
            case "icon" -> buildIcon(el, conditions);
            case "image" -> buildImage(el, conditions);
            case "input" -> buildInput(el, conditions);
            default -> throw new MarkupException("unknown tag <" + tag + ">");
        };
    }

    private static Component<?> buildContainer(Container<?> container, Element el, ConditionRegistry conditions) {
        applyCommonAttributes(container, el, conditions);
        for (Element child : children(el)) {
            Component<?> built = buildNode(child, conditions);
            if (built != null) {
                container.add(built);
            }
        }
        return container;
    }

    private static Button buildButton(Element el, ConditionRegistry conditions) {
        Button button = new Button();
        applyCommonAttributes(button, el, conditions);
        button.text(normalizeWhitespace(el.getTextContent()));

        String icon = attr(el, "icon", null);
        if (icon != null) {
            button.icon(icon);
        }
        String tooltip = attr(el, "tooltip", null);
        if (tooltip != null) {
            button.tooltip(tooltip);
        }
        String action = attr(el, "action", null);
        if (action != null) {
            button.action(action);
        }
        String width = attr(el, "width", null);
        if (width != null) {
            button.width(Integer.parseInt(width.trim()));
        }
        String enabledIf = attr(el, "enabled-if", null);
        if (enabledIf != null) {
            button.enabledIf(conditions.parse(enabledIf));
        }
        return button;
    }

    private static Text buildText(Element el, ConditionRegistry conditions) {
        Text text = new Text();
        applyCommonAttributes(text, el, conditions);
        text.content(normalizeWhitespace(el.getTextContent()));
        return text;
    }

    private static Icon buildIcon(Element el, ConditionRegistry conditions) {
        Icon icon = new Icon();
        applyCommonAttributes(icon, el, conditions);

        String material = attr(el, "material", null);
        if (material == null) {
            material = normalizeWhitespace(el.getTextContent());
        }
        icon.material(material);

        String count = attr(el, "count", null);
        if (count != null) {
            icon.count(Integer.parseInt(count.trim()));
        }
        String showTooltip = attr(el, "show-tooltip", null);
        if (showTooltip != null) {
            icon.showTooltip(Boolean.parseBoolean(showTooltip.trim()));
        }
        return icon;
    }

    private static Image buildImage(Element el, ConditionRegistry conditions) {
        Image image = new Image();
        applyCommonAttributes(image, el, conditions);

        String src = attr(el, "src", attr(el, "source", null));
        if (src != null) {
            image.source(src);
        }
        String fallback = attr(el, "fallback-icon", null);
        if (fallback != null) {
            image.fallbackIcon(fallback);
        }
        return image;
    }

    private static Input buildInput(Element el, ConditionRegistry conditions) {
        String key = attr(el, "key", attr(el, "id", null));
        if (key == null) {
            throw new MarkupException("<input> requires a key or id attribute");
        }
        Input input = new Input(key);
        applyCommonAttributes(input, el, conditions);

        String label = attr(el, "label", null);
        if (label != null) {
            input.label(label);
        }

        String type = attr(el, "type", "text").toLowerCase();
        switch (type) {
            case "boolean", "bool" -> input.initialBoolean(Boolean.parseBoolean(attr(el, "initial", "false")));
            case "number", "number-range" -> {
                float min = Float.parseFloat(attr(el, "min", "0"));
                float max = Float.parseFloat(attr(el, "max", "100"));
                float step = Float.parseFloat(attr(el, "step", "1"));
                float initial = Float.parseFloat(attr(el, "initial", Float.toString(min)));
                input.numberRange(min, max, step, initial);
            }
            case "option", "single-option" -> {
                String optionsAttr = attr(el, "options", "");
                input.options(optionsAttr.isBlank() ? new String[0] : optionsAttr.trim().split("\\s*,\\s*"));
            }
            default -> input.initialText(attr(el, "initial", ""));
        }

        String width = attr(el, "width", null);
        if (width != null) {
            input.width(Integer.parseInt(width.trim()));
        }
        return input;
    }

    private static void applyCommonAttributes(Component<?> component, Element el, ConditionRegistry conditions) {
        String id = attr(el, "id", null);
        if (id != null) {
            component.id(id);
        }
        String cssClass = attr(el, "class", null);
        if (cssClass != null) {
            for (String c : cssClass.trim().split("\\s+")) {
                component.cssClass(c);
            }
        }
        String visibleIf = attr(el, "visible-if", null);
        if (visibleIf != null) {
            component.visibleIf(conditions.parse(visibleIf));
        }
        String permission = attr(el, "permission", null);
        if (permission != null) {
            component.permission(permission);
        }
        String style = attr(el, "style", null);
        if (style != null) {
            for (Map.Entry<String, String> e : CssParser.parseDeclarations(style).entrySet()) {
                component.style(e.getKey(), e.getValue());
            }
        }
        for (String shorthand : new String[] {"gap", "align", "direction", "order"}) {
            String value = attr(el, shorthand, null);
            if (value != null) {
                component.style(shorthand, value);
            }
        }
    }

    private static void applyCss(Component<?> node, Map<String, Map<String, String>> rules) {
        for (String cls : node.cssClasses()) {
            Map<String, String> declarations = rules.get(cls);
            if (declarations == null) {
                continue;
            }
            for (Map.Entry<String, String> e : declarations.entrySet()) {
                if (!node.style().containsKey(e.getKey())) {
                    node.style(e.getKey(), e.getValue());
                }
            }
        }
        if (node instanceof Container<?> container) {
            for (Component<?> child : container.children()) {
                applyCss(child, rules);
            }
        }
    }

    private static String attr(Element el, String name, String defaultValue) {
        if (!el.hasAttribute(name)) {
            return defaultValue;
        }
        return el.getAttribute(name);
    }

    private static List<Element> children(Element parent) {
        List<Element> result = new ArrayList<>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element element) {
                result.add(element);
            }
        }
        return result;
    }

    private static String normalizeWhitespace(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s+", " ");
    }

    private static Document parseDocument(String markup) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(markup)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new MarkupException("failed to parse HTMLib markup: " + e.getMessage(), e);
        }
    }
}
