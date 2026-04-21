package com.nitsha.binds.utils;

import com.nitsha.binds.configs.dto.actions.AllActionsData;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.List;
import java.util.Map;

public class FormattedTextUtils {

    public static MutableComponent buildComponent(AllActionsData.TextFormatData data) {
        if (data == null || data.text == null || data.text.isEmpty()) return TextUtils.empty();
        MutableComponent message = TextUtils.empty();
        if (data.marks != null && !data.marks.isEmpty()) {
            Style currentStyle = Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(0xFFFFFF));
            int lastPos = 0;
            for (Map<String, Integer> markData : data.marks) {
                int pos = markData.getOrDefault("pos", 0);
                int styleCode = markData.getOrDefault("style", 0);
                if (pos > lastPos) {
                    message = message.append(TextUtils.literal(data.text.substring(lastPos, pos)).setStyle(currentStyle));
                }
                currentStyle = applyStyleCode(styleCode, currentStyle);
                lastPos = pos;
            }
            if (lastPos < data.text.length()) {
                message = message.append(TextUtils.literal(data.text.substring(lastPos)).setStyle(currentStyle));
            }
        } else {
            message = TextUtils.literal(data.text);
        }
        return message;
    }

    public static MutableComponent buildComponent(AllActionsData.TextFormatData data, int maxChars) {
        if (data == null || data.text == null || data.text.isEmpty()) return TextUtils.empty();

        String workText = data.text;
        if (workText.length() > maxChars) {
            workText = workText.substring(0, maxChars) + "...";
        }

        MutableComponent message = TextUtils.empty();
        if (data.marks != null && !data.marks.isEmpty()) {
            Style currentStyle = Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(0xFFFFFF));
            int lastPos = 0;
            for (Map<String, Integer> markData : data.marks) {
                int pos = markData.getOrDefault("pos", 0);
                if (pos > maxChars) continue;
                int styleCode = markData.getOrDefault("style", 0);
                if (pos > lastPos) {
                    message = message.append(TextUtils.literal(workText.substring(lastPos, pos)).setStyle(currentStyle));
                }
                currentStyle = applyStyleCode(styleCode, currentStyle);
                lastPos = pos;
            }
            if (lastPos < workText.length()) {
                message = message.append(TextUtils.literal(workText.substring(lastPos)).setStyle(currentStyle));
            }
        } else {
            message = TextUtils.literal(workText);
        }
        return message;
    }

    public static MutableComponent buildFormattedComponent(Map<String, Object> formattedText) {
        String text = (String) formattedText.get("text");
        MutableComponent message = TextUtils.empty();

        if (text == null || text.isEmpty()) return message;
        if (!formattedText.containsKey("marks")) return TextUtils.literal(text);

        List<Map<String, Object>> marks = (List<Map<String, Object>>) formattedText.get("marks");
        if (marks.isEmpty()) return TextUtils.literal(text);

        marks.sort((a, b) -> {
            int posA = ((Number) a.get("pos")).intValue();
            int posB = ((Number) b.get("pos")).intValue();
            return Integer.compare(posA, posB);
        });

        Style currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
        int lastPos = 0;

        for (Map<String, Object> markData : marks) {
            int pos = ((Number) markData.get("pos")).intValue();
            int styleCode = ((Number) markData.get("style")).intValue();

            if (pos > lastPos && pos <= text.length()) {
                message = message.append(TextUtils.literal(text.substring(lastPos, pos)).setStyle(currentStyle));
            }

            currentStyle = applyStyleCode(styleCode, currentStyle);
            lastPos = pos;
        }

        if (lastPos < text.length()) {
            message = message.append(TextUtils.literal(text.substring(lastPos)).setStyle(currentStyle));
        }

        return message;
    }

    public static Style applyStyleCode(int code, Style baseStyle) {
        if (code >= 0 && code <= 15) {
            int[] colors = {
                    0x1a1a1a, 0x0000AA, 0x00AA00, 0x00AAAA,
                    0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
                    0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
                    0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
            };
            Style newStyle = baseStyle.withColor(TextColor.fromRgb(colors[code]));
            if (baseStyle.isBold())       newStyle = newStyle.withBold(true);
            if (baseStyle.isItalic())     newStyle = newStyle.withItalic(true);
            if (baseStyle.isUnderlined()) newStyle = newStyle.withUnderlined(true);
            //? if >=1.17 {
            if (baseStyle.isStrikethrough()) newStyle = newStyle.withStrikethrough(true);
            if (baseStyle.isObfuscated())    newStyle = newStyle.withObfuscated(true);
            //?} else {
            /*if (baseStyle.isStrikethrough()) newStyle = newStyle.applyFormat(ChatFormatting.STRIKETHROUGH);
            if (baseStyle.isObfuscated())    newStyle = newStyle.applyFormat(ChatFormatting.OBFUSCATED);*/
            //?}
            return newStyle;
        }

        switch (code) {
            case 20: return baseStyle.withBold(true);
            case 21: return baseStyle.withItalic(true);
            case 22: return baseStyle.withUnderlined(true);
            case 23:
                //? if >=1.17 {
                return baseStyle.withStrikethrough(true);
            //?} else {
            /*return baseStyle.applyFormat(ChatFormatting.STRIKETHROUGH);*/
            //?}
            case 24:
                //? if >=1.17 {
                return baseStyle.withObfuscated(true);
            //?} else {
            /*return baseStyle.applyFormat(ChatFormatting.OBFUSCATED);*/
            //?}
            case 99:
                return Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
            default:
                return baseStyle;
        }
    }
}