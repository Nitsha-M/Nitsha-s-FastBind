package com.nitsha.binds.utils;

public class FastbindParser {
    public static ParsedEntry parse(String input) {
        String prefix = "nitsha::fastbind-";
        if (!input.startsWith(prefix)) {
            return new ParsedEntry(1, "");
        }

        String withoutPrefix = input.substring(prefix.length());
        String[] parts = withoutPrefix.split("::", 2);

        if (parts.length < 2) {
            return null;
        }

        int type = actionType(parts[0]);
        String value = parts[1];

        return new ParsedEntry(type, value);
    }

    public static String toAction(int type, String input) {
        return "nitsha::fastbind-" + switch (type) {
            case 1 -> "command";
            case 2 -> "delay";
            case 3 -> "keybind";
            default -> -1;
        } + "::" + input;
    }

    private static int actionType(String typeText) {
        return switch (typeText) {
            case "command" -> 1;
            case "delay" -> 2;
            case "keybind" -> 3;
            default -> -1;
        };
    }

    public static class ParsedEntry {
        public final int type;
        public final String value;

        public ParsedEntry(int type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}