package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

//? if >=1.17 {
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

import java.util.*;

public class TextField extends AbstractButton {
    private static TextField focusedField = null;

    private final boolean isNumerical;
    private Runnable escapeEvent;
    private Runnable enterEvent;

    private static final ResourceLocation NORMAL = Main.id("textures/gui/test/text_field_normal.png");
    private static final ResourceLocation FOCUS = Main.id("textures/gui/test/text_field_focus.png");

    private final Font font;
    private String text = "";
    private Component placeholder;
    private int maxLength = 32;

    private int selectionStart = 0;
    private int selectionEnd = 0;
    private int firstCharacterIndex = 0;

    private long lastSwitchFocusTime = Util.getMillis();

    private float phScale = 1f;
    private float phTextX = 0f;
    private float phTextY = 0f;
    private float phBoxY = 0f;
    private boolean phInit = false;

    private boolean isAnimatedPlaceholder = true;

    private int x, y, width, height;

    public static class FormatMark implements Comparable<FormatMark> {
        public int position;
        public int styleCode;

        public FormatMark(int position, int styleCode) {
            this.position = position;
            this.styleCode = styleCode;
        }

        @Override
        public int compareTo(FormatMark other) {
            int posCompare = Integer.compare(this.position, other.position);
            if (posCompare != 0)
                return posCompare;
            return Integer.compare(getPriority(this.styleCode), getPriority(other.styleCode));
        }

        private int getPriority(int code) {
            if (code == 99)
                return 0;
            if (code >= 0 && code <= 15)
                return 1;
            return 2;
        }
    }

    private final List<FormatMark> formatMarks = new ArrayList<>();

    public TextField(Font font, int x, int y, int width, int height, int length, String text, String placeholder,
            boolean isNumerical) {
        super(x, y, width, height, TextUtils.literal(text));
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.font = font;
        this.text = String.valueOf(text);
        this.placeholder = TextUtils.literal(placeholder);
        this.isNumerical = isNumerical;
        this.maxLength = length;
    }

    public TextField(Font font, int x, int y, int width, int height, int length, String text, String placeholder) {
        this(font, x, y, width, height, length, text, placeholder, false);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setAnimatedPlaceholder(boolean animated) {
        isAnimatedPlaceholder = animated;
    }

    // Misc
    //? if >=1.21.9 {
    /* public static boolean hasControlDown() {
        Window handle = Minecraft.getInstance().getWindow();
        if (Util.getPlatform() == Util.OS.OSX) {
            return InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SUPER) ||
                   InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SUPER);
        } else {
            return InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL) ||
                   InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_CONTROL);
        }
    }
    public static boolean hasShiftDown() {
        Window handle = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT) ||
               InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
    public static boolean hasAltDown() {
        Window handle = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_ALT) ||
               InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_ALT);
    }*/
    //? } else {
        public static boolean hasControlDown() {
            return Screen.hasControlDown();
        }
        public static boolean hasShiftDown() {
            return Screen.hasShiftDown();
        }
        public static boolean hasAltDown() {
            return Screen.hasAltDown();
        }
    //? }

    //? if >=1.21.9 {
    /*public static boolean isCut(int i) {
        return i == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    public static boolean isPaste(int i) {
        return i == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    public static boolean isCopy(int i) {
        return i == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }
    public static boolean isSelectAll(int i) {
        return i == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }*/
    //? } else {
        public static boolean isCut(int i) {
            return Screen.isCut(i);
        }
        public static boolean isPaste(int i) {
            return Screen.isPaste(i);
        }
        public static boolean isCopy(int i) {
            return Screen.isCopy(i);
        }
        public static boolean isSelectAll(int i) {
            return Screen.isSelectAll(i);
        }
    //? }

    public void setStyle(int styleCode, int position) {
        if (position < 0 || position > text.length())
            return;

        if (styleCode == 99) {
            formatMarks.removeIf(mark -> mark.position == position);
        } else if (styleCode >= 0 && styleCode <= 15) {
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode >= 0 && mark.styleCode <= 15);
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode == 99);
        } else if (styleCode >= 20 && styleCode <= 24) {
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode == styleCode);
            formatMarks.removeIf(mark -> mark.position == position && mark.styleCode == 99);
        }

        formatMarks.add(new FormatMark(position, styleCode));
        Collections.sort(formatMarks);
    }

    public void setStyle(int styleCode) {
        if (!isFocused())
            return;

        if (selectionStart != selectionEnd) {
            setStyleToSelection(styleCode);
        } else {
            setStyle(styleCode, selectionStart);
        }
    }

    public void setStyleToSelection(int styleCode) {
        if (selectionStart == selectionEnd) {
            setStyle(styleCode, selectionStart);
            return;
        }

        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);

        setStyle(styleCode, start);

        Style styleBefore = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));

        for (FormatMark mark : formatMarks) {
            if (mark.position < start) {
                styleBefore = applyStyleCode(mark.styleCode, styleBefore);
            } else if (mark.position == start) {
                continue;
            } else {
                break;
            }
        }

        if (styleCode >= 0 && styleCode <= 15) {
            Integer previousColor = null;
            for (FormatMark mark : formatMarks) {
                if (mark.position < start && mark.styleCode >= 0 && mark.styleCode <= 15) {
                    previousColor = mark.styleCode;
                }
            }

            int restoreColor = (previousColor != null) ? previousColor : 15;

            formatMarks.removeIf(mark -> mark.position == end && mark.styleCode >= 0 && mark.styleCode <= 15);
            formatMarks.add(new FormatMark(end, restoreColor));
        } else if (styleCode >= 20 && styleCode <= 24) {
            boolean wasStyleActive = false;
            Style checkStyle = Style.EMPTY;

            for (FormatMark mark : formatMarks) {
                if (mark.position < start) {
                    checkStyle = applyStyleCode(mark.styleCode, checkStyle);
                }
            }

            switch (styleCode) {
                case 20:
                    wasStyleActive = checkStyle.isBold();
                    break;
                case 21:
                    wasStyleActive = checkStyle.isItalic();
                    break;
                case 22:
                    wasStyleActive = checkStyle.isUnderlined();
                    break;
                case 23:
                    wasStyleActive = checkStyle.isStrikethrough();
                    break;
                case 24:
                    wasStyleActive = checkStyle.isObfuscated();
                    break;
            }

            if (!wasStyleActive) {
                formatMarks.removeIf(mark -> mark.position == end && mark.styleCode == styleCode);
            }
        } else if (styleCode == 99) {
            formatMarks.removeIf(mark -> mark.position == end && mark.styleCode == 99);
        }

        Collections.sort(formatMarks);
    }

    public void removeStyle(int position) {
        formatMarks.removeIf(mark -> mark.position == position);
    }

    public void removeStyle() {
        int position = isFocused() ? selectionStart : text.length();
        removeStyle(position);
    }

    public void clearStyles() {
        formatMarks.clear();
    }

    public List<FormatMark> getFormatMarks() {
        return new ArrayList<>(formatMarks);
    }

    public void setFormatMarks(List<FormatMark> marks) {
        formatMarks.clear();
        formatMarks.addAll(marks);
        Collections.sort(formatMarks);
    }

    public List<Map<String, Integer>> getFormatMarksAsMap() {
        List<Map<String, Integer>> result = new ArrayList<>();
        for (FormatMark mark : formatMarks) {
            Map<String, Integer> markData = new HashMap<>();
            markData.put("pos", mark.position);
            markData.put("style", mark.styleCode);
            result.add(markData);
        }
        return result;
    }

    public void setFormatMarksFromMap(List<Map<String, Object>> marks) {
        formatMarks.clear();
        for (Map<String, Object> markData : marks) {
            int pos = ((Number) markData.get("pos")).intValue();
            int style = ((Number) markData.get("style")).intValue();
            formatMarks.add(new FormatMark(pos, style));
        }
        Collections.sort(formatMarks);
    }

    private Style applyStyleCode(int code, Style baseStyle) {
        if (code >= 0 && code <= 15) {
            int[] colors = {
                    0x1a1a1a, 0x0000AA, 0x00AA00, 0x00AAAA,
                    0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
                    0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
                    0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
            };
            Style newStyle = baseStyle.withColor(TextColor.fromRgb(colors[code]));
            if (baseStyle.isBold())
                newStyle = newStyle.withBold(true);
            if (baseStyle.isItalic())
                newStyle = newStyle.withItalic(true);
            if (baseStyle.isUnderlined())
                newStyle = newStyle.withUnderlined(true);
            //? if >=1.17 {
            if (baseStyle.isStrikethrough())
                newStyle = newStyle.withStrikethrough(true);
            if (baseStyle.isObfuscated())
                newStyle = newStyle.withObfuscated(true);
            //?} else {
            /*if (baseStyle.isStrikethrough())
                newStyle = newStyle.applyFormat(ChatFormatting.STRIKETHROUGH);
            if (baseStyle.isObfuscated())
                newStyle = newStyle.applyFormat(ChatFormatting.OBFUSCATED);*/
            //?}
            return newStyle;
        }

        switch (code) {
            case 20:
                return baseStyle.withBold(true);
            case 21:
                return baseStyle.withItalic(true);
            case 22:
                return baseStyle.withUnderlined(true);
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

    private List<StyledSegment> buildStyledSegments(String text, int startFrom, int endAt) {
        List<StyledSegment> segments = new ArrayList<>();

        if (text.isEmpty())
            return segments;

        Style currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
        int lastPos = startFrom;

        for (FormatMark mark : formatMarks) {
            if (mark.position < startFrom) {
                currentStyle = applyStyleCode(mark.styleCode, currentStyle);
                continue;
            }

            if (mark.position >= endAt)
                break;

            if (mark.position > lastPos) {
                String segText = text.substring(lastPos, mark.position);
                segments.add(new StyledSegment(segText, currentStyle));
            }

            currentStyle = applyStyleCode(mark.styleCode, currentStyle);
            lastPos = mark.position;
        }

        if (lastPos < endAt) {
            String segText = text.substring(lastPos, endAt);
            segments.add(new StyledSegment(segText, currentStyle));
        }

        return segments;
    }

    private static class StyledSegment {
        String text;
        Style style;

        StyledSegment(String text, Style style) {
            this.text = text;
            this.style = style;
        }
    }

    private void updateFormatMarksAfterEdit(int editPos, int lengthChange) {
        if (lengthChange == 0)
            return;

        if (lengthChange < 0) {
            int deleteStart = editPos;
            int deleteEnd = editPos - lengthChange;
            formatMarks.removeIf(mark -> mark.position >= deleteStart && mark.position < deleteEnd);
        }

        for (FormatMark mark : formatMarks) {
            if (lengthChange > 0) {
                if (mark.position > editPos) {
                    mark.position += lengthChange;
                }
            } else {
                if (mark.position >= editPos) {
                    mark.position += lengthChange;
                }
            }
        }

        formatMarks.removeIf(mark -> mark.position < 0 || mark.position > text.length());
        Collections.sort(formatMarks);
    }

    public void setEscapeEvent(Runnable event) {
        this.escapeEvent = event;
    }

    public void setEnterEvent(Runnable event) {
        this.enterEvent = event;
    }

    public Runnable getEscapeEvent() {
        return this.escapeEvent;
    }

    public Runnable getEnterEvent() {
        return this.enterEvent;
    }

    public void setPlaceholder(Component placeholder) {
        this.placeholder = placeholder;
    }

    public void setText(String text) {
        if (text.length() > this.maxLength) {
            this.text = text.substring(0, this.maxLength);
        } else {
            this.text = text;
        }
        this.setCursorToEnd(false);
        this.formatMarks.clear();
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void setCursor(int position, boolean shiftKeyPressed) {
        this.selectionStart = Mth.clamp(position, 0, this.text.length());

        if (!shiftKeyPressed) {
            this.selectionEnd = this.selectionStart;
        }

        this.updateFirstCharacterIndex(this.selectionStart);
    }

    public void setCursorToStart(boolean shiftKeyPressed) {
        this.setCursor(0, shiftKeyPressed);
    }

    public void setCursorToEnd(boolean shiftKeyPressed) {
        this.setCursor(this.text.length(), shiftKeyPressed);
    }

    private void moveCursor(int offset, boolean shiftKeyPressed) {
        this.setCursor(Util.offsetByCodepoints(this.text, this.selectionStart, offset), shiftKeyPressed);
    }

    private int getWordSkipPosition(int wordOffset) {
        int pos = this.selectionStart;
        boolean backwards = wordOffset < 0;
        int count = Math.abs(wordOffset);

        for (int i = 0; i < count; i++) {
            if (!backwards) {
                int len = this.text.length();
                pos = this.text.indexOf(32, pos);
                if (pos == -1) {
                    pos = len;
                } else {
                    while (pos < len && this.text.charAt(pos) == ' ') {
                        pos++;
                    }
                }
            } else {
                while (pos > 0 && this.text.charAt(pos - 1) == ' ') {
                    pos--;
                }
                while (pos > 0 && this.text.charAt(pos - 1) != ' ') {
                    pos--;
                }
            }
        }
        return pos;
    }

    public void write(String input) {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        int availableSpace = this.maxLength - this.text.length() - (i - j);

        if (availableSpace > 0) {
            String filtered = input;

            if (this.isNumerical) {
                filtered = filtered.replaceAll("[^0-9]", "");
            }

            int len = filtered.length();
            if (availableSpace < len) {
                filtered = filtered.substring(0, availableSpace);
                len = availableSpace;
            }

            String newText = new StringBuilder(this.text)
                    .replace(i, j, filtered)
                    .toString();

            int lengthChange = len - (j - i);
            this.text = newText;

            updateFormatMarksAfterEdit(i, lengthChange);

            this.setCursor(i + len, false);
        }
    }

    private void erase(int offset) {
        if (hasControlDown()) {
            this.eraseWords(offset);
        } else {
            this.eraseCharacters(offset);
        }
    }

    private void eraseCharacters(int characterOffset) {
        this.eraseCharactersTo(Util.offsetByCodepoints(this.text, this.selectionStart, characterOffset));
    }

    private void eraseWords(int wordOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                this.eraseCharactersTo(this.getWordSkipPosition(wordOffset));
            }
        }
    }

    private void eraseCharactersTo(int position) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                int i = Math.min(position, this.selectionStart);
                int j = Math.max(position, this.selectionStart);
                if (i != j) {
                    this.text = new StringBuilder(this.text).delete(i, j).toString();

                    // Обновляем метки форматирования
                    updateFormatMarksAfterEdit(i, -(j - i));

                    this.setCursor(i, false);
                }
            }
        }
    }

    private void updateFirstCharacterIndex(int cursor) {
        if (this.font != null) {
            this.firstCharacterIndex = Math.min(this.firstCharacterIndex, this.text.length());
            int innerWidth = this.width - 8;

            String visibleText = styledSubstrByWidth(this.firstCharacterIndex, innerWidth);
            int visibleEnd = this.firstCharacterIndex + visibleText.length();

            if (cursor == this.firstCharacterIndex) {
                String trimmed = styledSubstrByWidth(Math.max(0, this.selectionStart - innerWidth / 4), innerWidth);
                this.firstCharacterIndex = Math.max(0, this.firstCharacterIndex - trimmed.length());
            }

            if (cursor > visibleEnd) {
                this.firstCharacterIndex += cursor - visibleEnd;
            } else if (cursor <= this.firstCharacterIndex) {
                this.firstCharacterIndex = Math.max(0, this.firstCharacterIndex - (this.firstCharacterIndex - cursor));
            }

            this.firstCharacterIndex = Mth.clamp(this.firstCharacterIndex, 0, this.text.length());
        }
    }

    private int styledWidth(int from, int to) {
        List<StyledSegment> segments = buildStyledSegments(this.text, from, to);
        int w = 0;
        for (StyledSegment seg : segments) {
            w += font.width(TextUtils.literal(seg.text).setStyle(seg.style));
        }
        return w;
    }

    private String styledSubstrByWidth(int startIndex, int maxWidth) {
        int w = 0;
        int i = startIndex;
        while (i < this.text.length()) {
            int charW = styledWidth(i, i + 1);
            if (w + charW > maxWidth) break;
            w += charW;
            i++;
        }
        return this.text.substring(startIndex, i);
    }

    @Override
    public void onPress() {}

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        this.isHovered = isMouseOver(mouseX, mouseY);
        GUIUtils.drawResizableBox(
                ctx,
                (this.isFocused() || isHovered) ? FOCUS : NORMAL,
                getX(), getY(), getWidth(), getHeight(),
                3, 7);

        int renderX = getX() + 4;
        int renderY = this.getY() + (this.height - 8) / 2;

        int innerWidth = this.width - 8;
        String fullText = this.text.substring(this.firstCharacterIndex);
        String visibleText = styledSubstrByWidth(this.firstCharacterIndex, innerWidth);
        int visibleLength = visibleText.length();
        int visibleEnd = this.firstCharacterIndex + visibleLength;

        if (this.selectionStart != this.selectionEnd) {
            int selStart = Math.min(this.selectionStart, this.selectionEnd);
            int selEnd = Math.max(this.selectionStart, this.selectionEnd);

            int relativeSelStart = Math.max(0, selStart - this.firstCharacterIndex);
            int relativeSelEnd = Math.min(visibleLength, selEnd - this.firstCharacterIndex);

            if (relativeSelStart < relativeSelEnd) {
                String beforeSel = visibleText.substring(0, relativeSelStart);
                String selection = visibleText.substring(relativeSelStart, relativeSelEnd);

                int selX1 = getX() + 4 + styledWidth(this.firstCharacterIndex, this.firstCharacterIndex + relativeSelStart);
                int selX2 = getX() + 4 + styledWidth(this.firstCharacterIndex, this.firstCharacterIndex + relativeSelEnd);

                GUIUtils.drawFill(ctx, selX1, renderY, selX2, renderY + 8, 0x8033AAFF);
            }
        }

        if (!visibleText.isEmpty()) {
            List<StyledSegment> segments = buildStyledSegments(this.text, this.firstCharacterIndex, visibleEnd);

            for (StyledSegment segment : segments) {
                if (segment.text.isEmpty())
                    continue;

                Component styledText = TextUtils.literal(segment.text).setStyle(segment.style);
                //? if >=26.1 {
                // ctx.text(this.font, styledText, renderX, renderY, 0xFFFFFFFF, true);
                //? } else if >=1.20 {
                ctx.drawString(this.font, styledText, renderX, renderY, 0xFFFFFFFF, true);
                //? } else {
                /*ctx.pushPose();
                 this.font.drawShadow((PoseStack)ctx, styledText, renderX, renderY, 0xFFFFFFFF);
                 ctx.popPose();*/
                //? }

                renderX += font.width(TextUtils.literal(segment.text).setStyle(segment.style));
            }
        }

        int cursorPos = this.selectionStart - this.firstCharacterIndex;
        boolean cursorVisible = cursorPos >= 0 && cursorPos <= visibleLength;
        boolean shouldBlink = (Util.getMillis() - this.lastSwitchFocusTime) / 300L % 2L == 0L;

        if (isFocused() && cursorVisible && shouldBlink) {
            String beforeCursor = visibleText.substring(0, cursorPos);
            int cursorX = getX() + 4 + styledWidth(this.firstCharacterIndex, this.firstCharacterIndex + cursorPos);
            GUIUtils.drawFill(ctx, cursorX, renderY, cursorX + 1, renderY + 8, 0xFFFFFFFF);
        }

        if (isAnimatedPlaceholder) {
            animatedPlaceholder(ctx);
        } else {
            if (!isFocused() && getText().isEmpty()) {
                GUIUtils.addText(
                        ctx,
                        TextUtils.literal(this.placeholder.getString()),
                        0,
                        getX() + 4,
                        getY() + (getHeight() - 8) / 2,
                        "top",
                        "left",
                        0xFFAAAAAA,
                        false);
            }
        }
    }

    private void animatedPlaceholder(GuiGraphics ctx) {
        float scaleFocused = 0.5f;
        float scaleUnfocused = 1f;

        float boxUnfocusedY = getY();
        float boxFocusedY = getY() - 3f;

        int textUnfocusedX = getX() + 4;
        int textFocusedX = getX() + 5;

        int textUnfocusedY = getY() + (getHeight() - 8) / 2;
        int textFocusedY = getY() - 1;

        boolean animatedPlaceholder = isFocused() || !this.getText().isEmpty();

        float wantScale = animatedPlaceholder ? scaleFocused : scaleUnfocused;
        float wantTextX = animatedPlaceholder ? textFocusedX : textUnfocusedX;
        float wantTextY = animatedPlaceholder ? textFocusedY : textUnfocusedY;
        float wantBoxY = animatedPlaceholder ? boxFocusedY : boxUnfocusedY;
        int wantPColor = animatedPlaceholder ? 0xFFFFFFFF : 0xFFAAAAAA;

        float smooth = 0.22f;
        phScale = Mth.lerp(smooth, phScale, wantScale);
        phTextX = Mth.lerp(smooth, phTextX, wantTextX);
        phTextY = Mth.lerp(smooth, phTextY, wantTextY);
        phBoxY = Mth.lerp(smooth, phBoxY, wantBoxY);

        if (!phInit) {
            phInit = true;
            phScale = wantScale;
            phTextX = wantTextX;
            phTextY = wantTextY;
            phBoxY = wantBoxY;
        }

        int rectLeft = getX() + 3;
        int rectTop = Math.round(phBoxY);
        int rectRight = rectLeft + Math.round(font.width(this.placeholder) * scaleFocused) + 4;
        int rectBottom = getY() + 1;
        int outlineColor = (isFocused() || isHovered) ? 0xFFFFFFFF : 0xFFA0A0A0;

        GUIUtils.drawFill(ctx, rectLeft + 1, rectTop, rectRight - 1, rectBottom - 1, outlineColor);
        GUIUtils.drawFill(ctx, rectLeft, rectTop + 1, rectRight, rectBottom - 1, outlineColor);
        GUIUtils.drawFill(ctx, rectLeft - 1, (animatedPlaceholder) ? rectTop + 2 : rectTop + 1, rectRight + 1,
                rectBottom - 1, outlineColor);

        GUIUtils.drawFill(ctx, rectLeft + 1, rectTop + 1, rectRight - 1, rectBottom, 0xFF000000);
        GUIUtils.drawFill(ctx, rectLeft, rectTop + 2, rectRight, rectBottom, 0xFF000000);

        GUIUtils.matricesScale(ctx, phScale, () -> {
            GUIUtils.addText(
                    ctx,
                    TextUtils.literal(this.placeholder.getString()),
                    0,
                    Math.round(phTextX / phScale),
                    Math.round(phTextY / phScale),
                    "top",
                    "left",
                    wantPColor,
                    false);
        });
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.text.length() > maxLength) {
            this.text = this.text.substring(0, maxLength);
            formatMarks.removeIf(mark -> mark.position > maxLength);
        }
    }

    public int getCursor() {
        return this.selectionStart;
    }

    public static TextField getFocusedField() {
        return focusedField;
    }

    public static void setFocusedField(TextField ff) {
        if (focusedField != null) {
            focusedField.setFocused(false);
        }
        focusedField = ff;
    }

    public void setFocus() {
        setLastClickedWidget(this);
        setFocusedField(this);
        super.setFocused(true);
        this.setCursorToEnd(false);
    }

    private static boolean blockFocus = false;
    private static GuiEventListener lastClickedWidget = null;

    public static void setLastClickedWidget(GuiEventListener widget) {
        lastClickedWidget = widget;
    }

    public static void setBlockFocus() {
        blockFocus = true;
    }

    public static void controlFocus() {
        if (focusedField == null) return;

        if (lastClickedWidget == focusedField) {
            return;
        }

        if (blockFocus) {
            blockFocus = false;
            return;
        }

        focusedField.setFocused(false);
        focusedField = null;
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (isMouseOver(mouseX, mouseY)) {
            setLastClickedWidget(this);
            setFocusedField(this);
            this.setFocused(true);

            int relativeX = Mth.floor(mouseX) - this.getX() - 4;
            String visibleText = this.font.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex),
                    this.width - 8);
            int clickedPos = this.font.plainSubstrByWidth(visibleText, relativeX).length() + this.firstCharacterIndex;
            this.setCursor(clickedPos, hasShiftDown());

            return true;
        }
        return false;
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                setLastClickedWidget(this);
                setFocusedField(this);
                this.setFocused(true);

                int relativeX = Mth.floor(mouseX) - this.getX() - 4;
                String visibleText = this.font.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex),
                        this.width - 8);
                int clickedPos = this.font.plainSubstrByWidth(visibleText, relativeX).length() + this.firstCharacterIndex;
                this.setCursor(clickedPos, hasShiftDown());

                return true;
            }
            return false;
        }
    //? }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //? } else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) { }*/
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
    int keyCode = event.key();
    int scanCode = event.scancode();*/
    //? } else {
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //? }
        if (!isFocused())
            return false;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT:
                if (hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(-1), hasShiftDown());
                } else {
                    this.moveCursor(-1, hasShiftDown());
                }
                return true;

            case GLFW.GLFW_KEY_RIGHT:
                if (hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(1), hasShiftDown());
                } else {
                    this.moveCursor(1, hasShiftDown());
                }
                return true;

            case GLFW.GLFW_KEY_HOME:
                this.setCursorToStart(hasShiftDown());
                return true;

            case GLFW.GLFW_KEY_END:
                this.setCursorToEnd(hasShiftDown());
                return true;

            case GLFW.GLFW_KEY_BACKSPACE:
                this.erase(-1);
                return true;

            case GLFW.GLFW_KEY_DELETE:
                this.erase(1);
                return true;

            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                setFocusedField(null);
                this.setFocused(false);
                if (this.enterEvent != null) {
                    this.enterEvent.run();
                }
                return true;

            case GLFW.GLFW_KEY_ESCAPE:
                setFocusedField(null);
                this.setFocused(false);
                if (this.escapeEvent != null) {
                    this.escapeEvent.run();
                }
                return true;

            default:
                if (isSelectAll(keyCode)) {
                    this.setCursorToEnd(false);
                    this.selectionEnd = 0;
                    return true;
                } else if (isCopy(keyCode)) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    return true;
                } else if (isPaste(keyCode)) {
                    this.write(Minecraft.getInstance().keyboardHandler.getClipboard());
                    return true;
                } else if (isCut(keyCode)) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    this.write("");
                    return true;
                }
                return false;
        }
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean charTyped(CharacterEvent event) {
        if (!isFocused())
            return false;
        char c = (char) event.codepoint();
        if (Character.isISOControl(c)) {
            return false;
        }
        this.write(Character.toString(c));
        return true;
    }*/
    //? } else {
    public boolean charTyped(char c, int modifiers) {
        if (!isFocused())
            return false;
        if (Character.isISOControl(c)) {
            return false;
        }
        this.write(Character.toString(c));
        return true;
    }
    //? }
}