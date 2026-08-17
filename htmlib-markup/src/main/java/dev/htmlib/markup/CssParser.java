package dev.htmlib.markup;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CssParser {

    private CssParser() {
    }

    public static Map<String, Map<String, String>> parse(String css) {
        Map<String, Map<String, String>> rules = new LinkedHashMap<>();
        if (css == null || css.isBlank()) {
            return rules;
        }

        String stripped = css.replaceAll("/\\*.*?\\*/", " ");
        int i = 0;
        int len = stripped.length();
        while (i < len) {
            int braceOpen = stripped.indexOf('{', i);
            if (braceOpen < 0) {
                break;
            }
            int braceClose = stripped.indexOf('}', braceOpen);
            if (braceClose < 0) {
                break;
            }

            String selectorPart = stripped.substring(i, braceOpen).trim();
            String body = stripped.substring(braceOpen + 1, braceClose).trim();
            Map<String, String> declarations = parseDeclarations(body);

            for (String selector : selectorPart.split(",")) {
                String cls = selector.trim();
                if (cls.startsWith(".")) {
                    cls = cls.substring(1).trim();
                }
                if (!cls.isEmpty()) {
                    rules.computeIfAbsent(cls, k -> new LinkedHashMap<>()).putAll(declarations);
                }
            }

            i = braceClose + 1;
        }
        return rules;
    }

    public static Map<String, String> parseDeclarations(String body) {
        Map<String, String> declarations = new LinkedHashMap<>();
        for (String statement : body.split(";")) {
            String s = statement.trim();
            if (s.isEmpty()) {
                continue;
            }
            int colon = s.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String key = s.substring(0, colon).trim();
            String value = s.substring(colon + 1).trim();
            if (!key.isEmpty()) {
                declarations.put(key, value);
            }
        }
        return declarations;
    }
}
